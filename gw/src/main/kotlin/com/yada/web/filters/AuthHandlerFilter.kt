package com.yada.web.filters

import com.yada.Filter
import com.yada.Next
import com.yada.security.AuthHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class AuthHandlerFilter(
        @Value("\${yada.contextPath:}")
        private val contextPath: String
) : Filter {

    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            AuthHolder.getUserInfo().flatMap { next(request) }.switchIfEmpty(
                    ServerResponse.seeOther(
                            UriComponentsBuilder.fromPath("$contextPath/login")
                                    .queryParam("redirect", request.uri().path)
                                    .build()
                                    .encode()
                                    .toUri()
                    ).build()
            )
}