package com.yada.web.filters

import com.yada.security.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class AuthHandlerFilter @Autowired constructor(jwtUtil: JwtTokenUtil) : Filter {
    private val filter = commonAuthHandlerFilter(
            jwtUtil,
            { authInfo -> authInfo != null },
            { request, _ ->
                val redirect = UriComponentsBuilder.fromPath("/login")
                        .queryParam("redirect", request.uri().path)
                        .build()
                        .encode()
                        .toUri()
                ServerResponse.seeOther(redirect).build()
            }
    )

    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            filter(request, next)
}