package com.yada.web.handlers.apis

import com.yada.web.model.Role
import com.yada.web.services.IRoleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class RoleHandler @Autowired constructor(private val roleService: IRoleService) {
    @Suppress("UNUSED_PARAMETER")
    fun getAll(req: ServerRequest): Mono<ServerResponse> = ok().body(roleService.getAll())

    fun get(req: ServerRequest): Mono<ServerResponse> = withNotFound(roleService.get(req.pathVariable("id")))
    fun exist(req: ServerRequest): Mono<ServerResponse> = ok().body(roleService.exist(req.pathVariable("id")))
    fun createOrUpdate(req: ServerRequest): Mono<ServerResponse> = ok().body(req.bodyToMono(Role::class.java).flatMap(roleService::createOrUpdate))
    fun delete(req: ServerRequest): Mono<ServerResponse> = ok().body(roleService.delete(req.pathVariable("id")))
}