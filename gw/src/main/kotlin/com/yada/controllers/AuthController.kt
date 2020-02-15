package com.yada.controllers

import com.yada.JwtTokenUtil
import com.yada.auths.IAuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.ValidationUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Controller
@RequestMapping("/")
class AuthController @Autowired constructor(private val jwtUtil: JwtTokenUtil, private val authService: IAuthenticationService) {

    data class LoginForm(val username: String?, val password: String?)
    data class ChangePwdData(val oldPwd: String?, val newPwd: String?)

    @GetMapping("login")
    fun login(model: Model): String {
        model.addAttribute("loginForm", LoginForm("", ""))
        return "auth/index"
    }

    @PostMapping("login")
    fun postLogin(@ModelAttribute loginForm: LoginForm, @RequestParam redirect: String, model: Model, exchange: ServerWebExchange): Mono<String> {
        val bindingResult = BeanPropertyBindingResult(loginForm, "loginForm")
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "username", "field.required")
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "password", "field.required")
        model.addAttribute(BindingResult.MODEL_KEY_PREFIX + bindingResult.objectName, bindingResult)

        return if (bindingResult.hasErrors()) {
            Mono.just("auth/index")
        } else {
            authService.login(loginForm.username!!, loginForm.password!!).map { token ->
                exchange.response.addCookie(jwtUtil.generateCookie(token))
                "redirect:${redirect}"
            }.switchIfEmpty(Mono.defer {
                bindingResult.reject("login.fail")
                Mono.just("auth/index")
            })
        }
    }

    @GetMapping("logout")
    fun logout(exchange: ServerWebExchange) {
        exchange.response.addCookie(jwtUtil.getEmptyCookie())
    }

    @PutMapping("change_pwd")
    fun changePwd(@CookieValue("token") token: String, @RequestBody data: ChangePwdData): Mono<Void> =
            if (data.oldPwd != null && data.newPwd != null) {
                val username = jwtUtil.getEntity(token)?.username!!
                authService.changePassword(username, data.oldPwd, data.newPwd)
                        .filter { it }
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                        .then()
            } else {
                Mono.empty()
            }
}
