package com.yada.web.handlers

import com.yada.security.JwtTokenUtil
import com.yada.security.authInfo
import com.yada.web.services.IAdminAuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class AdminAuthHandler @Autowired constructor(private val authSvc: IAdminAuthService, private val jwtUtil: JwtTokenUtil) {

    @Suppress("UNUSED_PARAMETER")
    fun index(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().render("/admin/index")

    fun login(req: ServerRequest): Mono<ServerResponse> {
        val dataMono: Mono<LoginData> = req.bodyToMono()
        return dataMono.flatMap {
            authSvc.login(it.username!!, it.password!!)
        }.flatMap {
            ServerResponse.ok().cookie(jwtUtil.generateCookie(it)).build()
        }.switchIfEmpty(Mono.error { ResponseStatusException(HttpStatus.CONFLICT, "登录失败") })
    }

    fun logout(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().cookie(jwtUtil.getEmptyCookie(req.authInfo)).build()

    fun changePwd(req: ServerRequest): Mono<ServerResponse> {
        val dataMono: Mono<ChangePwdData> = req.bodyToMono()
        return dataMono.flatMap { data ->
            if (data.oldPwd != null && data.newPwd != null) {
                authSvc.changePassword(req.authInfo.username!!, data.oldPwd, data.newPwd).flatMap { flag ->
                    if (flag)
                        ServerResponse.ok().build()
                    else
                        Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "密码错误"))
                }
            } else
                Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "密码不能为空"))
        }
    }

    fun refreshToken(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().cookie(jwtUtil.renewCookie(req.authInfo)).build()
}