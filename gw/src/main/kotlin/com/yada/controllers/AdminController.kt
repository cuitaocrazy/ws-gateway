package com.yada.controllers

import com.yada.JwtTokenUtil
import com.yada.services.IAdminAuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Controller
@RequestMapping("/admin")
class AdminController {
    @GetMapping
    fun index() = "admin/index"
}


@RestController
@RequestMapping("/admin")
class AdminAuthController @Autowired constructor(private val authSvc: IAdminAuthService, private val jwtUtil: JwtTokenUtil) {
    data class LoginForm(val username: String?, val password: String?)
    data class ChangePwdData(val oldPwd: String?, val newPwd: String?)

    @PostMapping("login")
    fun login(@RequestBody loginForm: LoginForm, exchange: ServerWebExchange): Mono<String> =
            authSvc.login(loginForm.username!!, loginForm.password!!)
            .map { token -> exchange.response.addCookie(jwtUtil.generateCookie(token)).run { token } }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "登录失败")))

    @PostMapping("logout")
    fun logout(exchange: ServerWebExchange) {
//        val cookie = exchange.request.cookies.toSingleValueMap()["token"]
//        val token = cookie?.value
//        if(token != null && jwtUtil.validateToken(token)) {
//
//        }
        exchange.response.addCookie(ResponseCookie.from("token", "").path("/admin").maxAge(0).build())
    }

    @PostMapping("apis/change_pwd")
    fun changePwd(@CookieValue("token") token: String, @RequestBody data: ChangePwdData): Mono<Void> =
            Mono.just(data.oldPwd != null && data.newPwd != null)
                    .filter { it }
                    .flatMap { authSvc.changePassword(jwtUtil.getEntity(token)?.username!!, data.oldPwd!!, data.newPwd!!) }
                    .filter { it }
                    .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                    .then()
}