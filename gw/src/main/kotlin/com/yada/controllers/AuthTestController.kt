package com.yada.controllers

import com.yada.auths.AuthenticationService
import com.yada.auths.AuthorizationService
import com.yada.model.Operator
import com.yada.services.IUserService
import com.yada.withNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthTestController @Autowired constructor(private val authenticationService: AuthenticationService, private val authorizationService: AuthorizationService, private val userService: IUserService){
    @GetMapping("login")
    fun login(@RequestParam username: String, @RequestParam("pwd") password: String) = withNotFound(authenticationService.login(username, password), "用户名或密码错误")

    @PutMapping("logout")
    fun logout(@RequestParam username: String) = authenticationService.logout(username)

    @PutMapping("changepwd")
    fun changePwd(@RequestParam username: String, @RequestParam("old_pwd") oldPassword: String, @RequestParam("new_pwd") newPassword: String) = authenticationService.changePassword(username, oldPassword, newPassword)

    @GetMapping("authorize")
    fun authorize(@RequestParam username: String, @RequestParam uri: String, @RequestParam opt: Operator): Mono<Boolean> = userService.get(username).flatMap { authorizationService.authorize(it, uri, opt) }.defaultIfEmpty(false)

    @GetMapping("userres")
    fun getUserResList(@RequestParam("app_id") appId: String, @RequestParam username: String) = userService.get(username).flatMapMany { authorizationService.getUserResList(appId, it) }
}