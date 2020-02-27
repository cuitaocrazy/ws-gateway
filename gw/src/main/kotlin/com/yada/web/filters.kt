package com.yada.web

import com.yada.AuthInfo
import com.yada.JwtTokenUtil
import com.yada.authInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.seeOther
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
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

@Component
class AuthHandlerFilter @Autowired constructor(jwtUtil: JwtTokenUtil) : Filter {
    private val filter = commonAuthHandlerFilter(
            jwtUtil,
            { authInfo -> authInfo != null },
            { request, _ ->
                val redirect = UriComponentsBuilder.fromPath("/login").queryParam("redirect", request.uri().path).build().encode().toUri()
                seeOther(redirect).build()
            }
    )

    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            filter(request, next)

}

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

@Component
class AuthAdminApiHandlerFilter @Autowired constructor(jwtUtil: JwtTokenUtil) : Filter {
    private val filter = commonAuthHandlerFilter(
            jwtUtil,
            { authInfo -> authInfo != null && authInfo.isAdmin },
            { _, _ -> Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED")) }
    )

    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            filter(request, next)

}

@Component
class WhitelistHandlerFilter @Autowired constructor(@Value("\${yada.admin.ipWhitelist}") private val whitelist: List<String>) : Filter {
    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            if (request.remoteAddress().isPresent && request.remoteAddress().get().address.toString() !in whitelist) {
                Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))
            } else {
                next(request)
            }
}