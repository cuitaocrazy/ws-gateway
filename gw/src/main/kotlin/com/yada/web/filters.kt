package com.yada.web

import com.yada.JwtTokenUtil
import com.yada.authInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.seeOther
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

typealias HandlerFilter = (ServerRequest, (ServerRequest) -> Mono<ServerResponse>) -> Mono<ServerResponse>

abstract class CommonAuthHandlerFilter(private val jwtUtil: JwtTokenUtil) : HandlerFilterFunction<ServerResponse, ServerResponse> {
    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
        val token = request.cookies()["token"]?.run { this[0]?.value }
        val jwtEntity = token?.run { jwtUtil.getEntity(this) }

        return if (jwtEntity != null) {
            request.authInfo = jwtEntity
            next.handle(request)
        } else {
            unauth(request, next)
        }
    }

    abstract fun unauth(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse>
}

@Component
class AuthHandlerFilter @Autowired constructor(jwtUtil: JwtTokenUtil) : CommonAuthHandlerFilter(jwtUtil) {
    override fun unauth(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
        val redirect = UriComponentsBuilder.fromPath("/login").queryParam("redirect", request.uri().path).build().encode().toUri()
        return seeOther(redirect).build()
    }
}

@Component
class AuthApiHandlerFilter @Autowired constructor(jwtUtil: JwtTokenUtil) : CommonAuthHandlerFilter(jwtUtil) {
    override fun unauth(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> =
            Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"))
}