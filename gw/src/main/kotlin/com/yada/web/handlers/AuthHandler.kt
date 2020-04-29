package com.yada.web.handlers

import com.nulabinc.zxcvbn.Zxcvbn
import com.yada.security.JwtTokenUtil
import com.yada.security.authInfo
import com.yada.web.model.Res
import com.yada.web.services.IAuthenticationService
import com.yada.web.services.IAuthorizationService
import com.yada.web.services.IRecaptchaService
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
        private val jwtUtil: JwtTokenUtil,
        private val authService: IAuthenticationService,
        private val authorServer: IAuthorizationService,
        @Value("\${yada.recaptcha}") recaptchaName: String,
        @Value("\${yada.password.strength.score:1}") private val score: Int,
        beans: BeanFactory) {
    private val recaptchaService: IRecaptchaService = beans.getBean(recaptchaName) as IRecaptchaService
    private val formBeanName = "loginForm"
    private val zxcvbn = Zxcvbn()

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
                            authService.login(form.username!!, form.password!!).doOnEach {
                                if (!it.hasValue()) {
                                    bindingResult.reject("login.fail")
                                }
                            }
                        }
                        .flatMap {
                            ServerResponse.seeOther(URI(redirect.orElse("/")))
                                    .cookie(jwtUtil.generateCookie(it))
                                    .build()
                        }
                        .switchIfEmpty(Mono.defer { ServerResponse.ok().render("/auth/index", model) })
            }

    fun logout(req: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().cookie(jwtUtil.getEmptyCookie(req.authInfo)).build()

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
                            if (zxcvbn.measure(newPwd).score < score) {
                                throw ResponseStatusException(HttpStatus.CONFLICT, "密码强度需要${score}")
                            }
                        }
                    }.flatMap { data ->
                        authService.changePassword(req.authInfo.username!!, data.oldPwd, data.newPwd)
                                .filter { it }
                                .switchIfEmpty(Mono.error { ResponseStatusException(HttpStatus.CONFLICT, "修改密码时出错") })
                                .then(ServerResponse.ok().build())
                    }

    fun refreshToken(req: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().cookie(jwtUtil.renewCookie(req.authInfo)).build()

    fun filterApis(req: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok()
                    .bodyValue(
                            req.bodyToMono<List<Res>>().flatMap {
                                authorServer.filterApis(it, req.authInfo.resList!!)
                            }
                    )
}