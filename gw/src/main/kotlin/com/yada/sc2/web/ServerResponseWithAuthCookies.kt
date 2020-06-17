package com.yada.sc2.web

import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class ServerResponseWithAuthCookies(private val req: ServerResponse, private val token: String) : ServerResponse by req {
    companion object {
        fun from(req: ServerResponse, token: String): ServerResponse = ServerResponseWithAuthCookies(req, token)
    }

    override fun writeTo(exchange: ServerWebExchange, context: ServerResponse.Context): Mono<Void> {
        exchange.response.addCookie(ResponseCookie.from(authCookiesKey, token).build())
        return req.writeTo(exchange, context)
    }
}