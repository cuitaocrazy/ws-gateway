package com.yada.web.filters

import com.yada.security.AuthInfo
import com.yada.security.JwtTokenUtil
import com.yada.security.authInfo
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

typealias Next = (ServerRequest) -> Mono<ServerResponse>
typealias Filter = (request: ServerRequest, next: Next) -> Mono<ServerResponse>
typealias Verify = (authInfo: AuthInfo?) -> Boolean
typealias Unauth = Filter

val commonAuthHandlerFilter = fun(jwtUtil: JwtTokenUtil, verify: Verify, unauth: Unauth): Filter = { request, next ->
    val token = request.cookies()["token"]?.run { this[0]?.value }
    val jwtEntity = token?.run { jwtUtil.getEntity(this) }
    if (verify(jwtEntity)) {
        request.authInfo = jwtEntity!!
        next(request)
    } else {
        unauth(request, next)
    }
}

