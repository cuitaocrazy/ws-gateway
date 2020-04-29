package com.yada.web.handlers.apis

import com.yada.web.model.User
import com.yada.web.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class UserHandler @Autowired constructor(private val userService: IUserService) {
    fun getUsersBy(req: ServerRequest): Mono<ServerResponse> =
            ok().body(userService.getByOrgId(req.queryParam("org_id").orElse("")))

    fun get(req: ServerRequest): Mono<ServerResponse> =
            withNotFound(userService.get(req.pathVariable("id")))

    fun exist(req: ServerRequest): Mono<ServerResponse> =
            ok().body(userService.exist(req.pathVariable("id")))

    fun createOrUpdate(req: ServerRequest): Mono<ServerResponse> =
            ok().body(req.bodyToMono(User::class.java).flatMap(userService::createOrUpdate))

    fun delete(req: ServerRequest): Mono<ServerResponse> =
            ok().body(userService.delete(req.pathVariable("id")))
}