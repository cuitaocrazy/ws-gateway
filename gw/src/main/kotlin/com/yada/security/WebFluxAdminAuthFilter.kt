package com.yada.security

import com.yada.Filter
import com.yada.Next
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class WebFluxAdminAuthFilter(private val adminAuthInfoParser: AdminAuthInfoParser) : Filter {
    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> {
        val adminInfo = adminAuthInfoParser.getInfo(request)
        if (adminInfo != null) {
            request.attributes()[isAdminKey] = true
            request.attributes()[adminInfoKey] = adminInfo
        } else {
            request.attributes()[isAdminKey] = false
        }

        return next(request)
    }
}