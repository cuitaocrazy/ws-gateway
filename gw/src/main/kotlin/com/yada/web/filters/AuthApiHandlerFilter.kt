package com.yada.web.filters

import com.yada.security.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class AuthApiHandlerFilter @Autowired constructor(jwtUtil: JwtTokenUtil) : Filter {
    private val filter = commonAuthHandlerFilter(
            jwtUtil,
            { authInfo -> authInfo != null },
            { _, _ -> Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED")) }
    )

    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            filter(request, next)

}