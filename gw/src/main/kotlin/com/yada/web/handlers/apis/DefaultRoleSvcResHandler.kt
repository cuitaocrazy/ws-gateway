package com.yada.web.handlers.apis

import com.yada.web.model.DefaultRoleSvcRes
import com.yada.web.services.IDefaultRoleSvcResService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class DefaultRoleSvcResHandler @Autowired constructor(
        private val defaultRoleSvcResService: IDefaultRoleSvcResService) {

    companion object {
        val defaultRoleSvcResListType = object : ParameterizedTypeReference<List<DefaultRoleSvcRes>>() {}
    }

    @Suppress("UNUSED_PARAMETER")
    fun get(req: ServerRequest): Mono<ServerResponse> =
            ok().body(defaultRoleSvcResService.get())

    fun createOrUpdate(req: ServerRequest) =
            ok().body(req.bodyToMono(defaultRoleSvcResListType).flatMap(defaultRoleSvcResService::createOrUpdate))
}