package com.yada.security

import com.yada.Filter
import com.yada.Next
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class WebFluxAuthFilter(private val authInfoParser: AuthInfoParser) : Filter {
    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            authInfoParser.run {
                val token = getToken(request)
                getAuthInfo(token).doOnSuccess {
                    request.attributes()["authInfo"] = it
                    request.attributes()["token"] = it
                }
            }.then(next(request))
}