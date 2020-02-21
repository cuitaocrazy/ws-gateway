package com.yada.web.handlers.apis

import com.yada.model.App
import com.yada.services.IAppService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class AppHandler @Autowired constructor(private val appService: IAppService) {
    @Suppress("UNUSED_PARAMETER")
    fun getAll(req: ServerRequest): Mono<ServerResponse> = ok().body(appService.getAll())

    fun get(req: ServerRequest): Mono<ServerResponse> = withNotFound(appService.get(req.pathVariable("id")))
    fun exist(req: ServerRequest): Mono<ServerResponse> = ok().body(appService.exist(req.pathVariable("id")))
    fun createOrUpdate(req: ServerRequest): Mono<ServerResponse> = ok().body(req.bodyToMono(App::class.java).flatMap(appService::createOrUpdate))
    fun delete(req: ServerRequest): Mono<ServerResponse> = ok().body(appService.delete(req.pathVariable("id")))
}