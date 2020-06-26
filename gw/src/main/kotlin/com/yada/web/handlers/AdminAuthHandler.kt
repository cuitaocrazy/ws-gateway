package com.yada.web.handlers

import com.yada.security.AuthHolder
import com.yada.web.services.IAdminUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class AdminAuthHandler @Autowired constructor(private val adminUserService: IAdminUserService) {

    @Suppress("UNUSED_PARAMETER")
    fun index(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().render("/admin/index")

    fun login(req: ServerRequest): Mono<ServerResponse> {
        val dataMono: Mono<LoginData> = req.bodyToMono()
        return dataMono.flatMap {
            AuthHolder.login(it.username!!, it.password!!)
        }.flatMap {
            ServerResponse.ok().build()
        }.switchIfEmpty(Mono.error { ResponseStatusException(HttpStatus.CONFLICT, "登录失败") })
    }

    @Suppress("UNUSED_PARAMETER")
    fun logout(req: ServerRequest): Mono<ServerResponse> =
            AuthHolder.logout().then(ServerResponse.ok().build())

    fun changePwd(req: ServerRequest): Mono<ServerResponse> {
        val dataMono: Mono<ChangePwdData> = req.bodyToMono()
        return dataMono.flatMap { data ->
            if (data.oldPwd != null && data.newPwd != null) {
                AuthHolder.getUserInfo().flatMap {
                    adminUserService.changePwd(it.userId, data.oldPwd, data.newPwd).flatMap { flag ->
                        if (flag)
                            ServerResponse.ok().build()
                        else
                            Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "密码错误"))
                    }
                }
            } else
                Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "密码不能为空"))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun refreshToken(req: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().build()
}