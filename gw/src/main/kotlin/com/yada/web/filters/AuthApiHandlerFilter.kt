package com.yada.web.filters

import com.yada.Filter
import com.yada.Next
import com.yada.security.authInfo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class AuthApiHandlerFilter : Filter {

    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            request.authInfo
                    .map { next(request) }
                    .orElse(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED")))

}