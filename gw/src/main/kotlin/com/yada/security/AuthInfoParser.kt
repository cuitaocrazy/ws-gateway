package com.yada.security

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


class AuthInfoParser(private val tokenManager: TokenManager, private val jwtTokenUtil: JwtTokenUtil) {
    fun getAuthInfo(token: String?): Mono<AuthInfo> = token?.run {
        tokenManager.get(token)
    } ?: Mono.empty()

    fun getToken(req: ServerRequest) = jwtTokenUtil.getToken(getJwt(req))
    fun getToken(exchange: ServerWebExchange) = jwtTokenUtil.getToken(getJwt(exchange))
    private fun getJwt(exchange: ServerWebExchange) = exchange.request.cookies[jwtTokenCookiesName]?.run { this[0]?.value }
    private fun getJwt(req: ServerRequest) = req.cookies()[jwtTokenCookiesName]?.run { this[0]?.value }
}