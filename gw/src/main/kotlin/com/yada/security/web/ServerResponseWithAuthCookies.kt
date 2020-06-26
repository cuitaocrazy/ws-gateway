package com.yada.security.web

import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class ServerResponseWithAuthCookies(private val req: ServerResponse, private val token: String, private val cookiePath: String) : ServerResponse by req {
    companion object {
        fun from(req: ServerResponse, token: String, cookiePath: String): ServerResponse = ServerResponseWithAuthCookies(req, token, cookiePath)
    }

    override fun writeTo(exchange: ServerWebExchange, context: ServerResponse.Context): Mono<Void> {
        if (token == "")
            exchange.response.addCookie(ResponseCookie.from(authCookiesKey, token).maxAge(0).path(cookiePath).build())
        else
            exchange.response.addCookie(ResponseCookie.from(authCookiesKey, token).path(cookiePath).build())
        return req.writeTo(exchange, context)
    }
}