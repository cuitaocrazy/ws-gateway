package com.yada.web.handlers

import com.yada.JwtTokenUtil
import com.yada.authInfo
import com.yada.services.IAuthenticationService
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
class AuthHandler @Autowired constructor(private val jwtUtil: JwtTokenUtil, private val authService: IAuthenticationService) {
    private val formBeanName = "loginForm"

    @Suppress("UNUSED_PARAMETER")
    fun getLoginForm(req: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().render("/auth/index", mapOf(formBeanName to LoginData("", "")))

    fun login(req: ServerRequest): Mono<ServerResponse> = req.formData()
            .flatMap {
                val redirect = req.queryParam("redirect")
                val map = it.toSingleValueMap()
                val form = LoginData(map["username"], map["password"])

                val bindingResult = BeanPropertyBindingResult(form, formBeanName)
                ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "username", "field.required")
                ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "password", "field.required")
                val model = mapOf(BindingResult.MODEL_KEY_PREFIX + bindingResult.objectName to bindingResult, formBeanName to form)

                if (bindingResult.hasErrors()) {
                    ServerResponse.ok().render("/auth/index", model)
                } else {
                    authService.login(form.username!!, form.password!!).flatMap { authInfo ->
                        ServerResponse.seeOther(URI(redirect.orElse("/"))).cookie(jwtUtil.generateCookie(authInfo)).build()
                    }.switchIfEmpty(kotlin.run {
                        bindingResult.reject("login.fail")
                        ServerResponse.ok().render("/auth/index", model)
                    })
                }
            }

    fun logout(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().cookie(jwtUtil.getEmptyCookie(req.authInfo)).build()

    data class ChangePwdData(val oldPwd: String?, val newPwd: String?)

    fun changePwd(req: ServerRequest): Mono<ServerResponse> {
        val dataMono: Mono<ChangePwdData> = req.bodyToMono()
        return dataMono.flatMap { data ->
            if (data.oldPwd != null && data.newPwd != null) {
                val username = req.authInfo.username!!
                authService.changePassword(username, data.oldPwd, data.newPwd)
                        .filter { it }
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "密码出错")))
                        .then(ServerResponse.ok().build())
            } else {
                Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "密码不能为空"))
            }
        }
    }

    fun refreshToken(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().cookie(jwtUtil.renewCookie(req.authInfo)).build()
}