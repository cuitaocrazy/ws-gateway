package com.yada.web.handlers

import com.yada.sc2.AuthHolder
import com.yada.security.IPwdStrengthService
import com.yada.security.IRecaptchaService
import com.yada.web.model.Res
import com.yada.web.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.ValidationUtils
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.net.URI


@Component
class AuthHandler @Autowired constructor(
        private val userService: IUserService,
        private val recaptchaService: IRecaptchaService,
        private val pwdStrengthService: IPwdStrengthService
) {
    private val formBeanName = "loginForm"

    @Suppress("UNUSED_PARAMETER")
    fun getLoginForm(req: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().render("/auth/index", mapOf(formBeanName to LoginData("", "")))

    fun login(req: ServerRequest): Mono<ServerResponse> = req.formData()
            .flatMap { it ->
                val redirect = req.queryParam("redirect")
                val map = it.toSingleValueMap()
                val form = LoginData(map["username"], map["password"])
                val recaptchaResponse = recaptchaService.getCode(map)

                val bindingResult = BeanPropertyBindingResult(form, formBeanName)
                ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "username", "field.required")
                ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "password", "field.required")
                val model = mapOf(
                        BindingResult.MODEL_KEY_PREFIX + bindingResult.objectName to bindingResult,
                        formBeanName to form
                )

                if (recaptchaResponse == null || recaptchaResponse == "") {
                    bindingResult.reject("login.must.recaptcha")
                }

                Mono.just(!bindingResult.hasErrors())
                        .filter { it }
                        .flatMap {
                            recaptchaService.check(recaptchaResponse!!)
                                    .doOnSuccess {
                                        if (!it) {
                                            bindingResult.reject("login.fail.recaptcha")
                                        }
                                    }
                        }
                        .filter { it }
                        .flatMap {
                            AuthHolder.login(form.username!!, form.password!!).doOnEach {
                                if (!it.hasValue()) {
                                    bindingResult.reject("login.fail")
                                }
                            }
                        }
                        .flatMap {
                            ServerResponse.seeOther(URI(redirect.orElse("/"))).build()
                        }
                        .switchIfEmpty(Mono.defer { ServerResponse.ok().render("/auth/index", model) })
            }

    @Suppress("UNUSED_PARAMETER")
    fun logout(req: ServerRequest): Mono<ServerResponse> =
            AuthHolder.logout().then(ServerResponse.ok().build())


    data class ChangePwdData(val oldPwd: String?, val newPwd: String?)

    fun changePwd(req: ServerRequest): Mono<ServerResponse> =
            req.bodyToMono<ChangePwdData>()
                    .filter {
                        it.oldPwd != null && it.newPwd != null
                    }
                    .map {
                        if (it.oldPwd != null && it.newPwd != null)
                            throw ResponseStatusException(HttpStatus.CONFLICT, "密码不能为空")
                        else
                            object {
                                val oldPwd = it.oldPwd!!
                                val newPwd = it.newPwd!!
                            }
                    }
                    .map {
                        it.apply {
                            if (!pwdStrengthService.checkStrength(newPwd)) {
                                throw ResponseStatusException(HttpStatus.CONFLICT, "密码强度不足")
                            }
                        }
                    }.flatMap { data ->
                        AuthHolder.getUserInfo()
                                .flatMap {
                                    userService.changePwd(it.userId, data.oldPwd, data.newPwd)
                                }
                                .filter { it }
                                .switchIfEmpty(Mono.error { ResponseStatusException(HttpStatus.CONFLICT, "修改密码时出错") })
                                .then(ServerResponse.ok().build())
                    }

    @Suppress("UNUSED_PARAMETER")
    fun refreshToken(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().build()

    fun filterApis(req: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok()
                    .bodyValue(
                            req.bodyToMono<List<Res>>().flatMap { resList ->
                                AuthHolder.getUserInfo().flatMap {
                                    userService.filterApis(resList, it.powers)
                                }
                            }
                    )


}