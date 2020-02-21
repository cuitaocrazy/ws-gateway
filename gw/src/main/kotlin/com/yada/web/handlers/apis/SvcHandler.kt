package com.yada.web.handlers.apis

import com.yada.model.Svc
import com.yada.services.SvcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class SvcHandler @Autowired constructor(private val svcService: SvcService) {
    fun getAll(req: ServerRequest): Mono<ServerResponse> = ok().body(svcService.getAll())
    fun get(req: ServerRequest): Mono<ServerResponse> = withNotFound(svcService.get(req.pathVariable("id")))
    fun createOrUpdate(req: ServerRequest): Mono<ServerResponse> = ok().body(req.bodyToMono(Svc::class.java).flatMap(svcService::createOrUpdate))
    fun delete(req: ServerRequest): Mono<ServerResponse> = ok().body(svcService.delete(req.pathVariable("id")))
}