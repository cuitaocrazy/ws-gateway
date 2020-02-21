package com.yada.web.handlers.apis

import com.yada.model.Org
import com.yada.services.IOrgService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class OrgHandler @Autowired constructor(private val orgService: IOrgService) {
    fun getTree(req: ServerRequest): Mono<ServerResponse> = ok().body(orgService.getTree(req.queryParam("redirect").orElse("")))
    fun get(req: ServerRequest): Mono<ServerResponse> = withNotFound(orgService.get(req.pathVariable("id")))
    fun createOrUpdate(req: ServerRequest): Mono<ServerResponse> = ok().body(req.bodyToMono(Org::class.java).flatMap(orgService::createOrUpdate))
    fun exist(req: ServerRequest): Mono<ServerResponse> = ok().body(orgService.exist(req.pathVariable("id")))
    fun delete(req: ServerRequest): Mono<ServerResponse> = ok().body(orgService.delete(req.pathVariable("id")))
}