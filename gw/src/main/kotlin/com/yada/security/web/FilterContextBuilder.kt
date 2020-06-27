package com.yada.security.web

import com.yada.security.Auth
import com.yada.security.AuthHolder
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

object FilterContextBuilder {
    fun buildGatewayFilter(auth: Auth, name: String, handle: GatewayFilterHandle): GatewayFilter = object : GatewayFilter {
        override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void>? {
            val token = exchange.request.cookies[authCookiesKey]?.run { this[0]?.value }

            exchange.response.beforeCommit {
                AuthHolder.getUserInfo()
                        .flatMap { AuthHolder.getToken() }
                        .defaultIfEmpty("").map {
                            if (it == "")
                                exchange.response.addCookie(ResponseCookie.from(authCookiesKey, it).maxAge(0).path(auth.getPath()).build())
                            else
                                exchange.response.addCookie(ResponseCookie.from(authCookiesKey, it).path(auth.getPath()).build())
                        }.then()
            }

            return AuthHolder.initContext(handle(exchange, chain), auth, token)
        }

        override fun toString(): String {
            return name
        }
    }

    fun buildDefaultFluxFilter(auth: Auth): FluxFilterFunction = { req, next ->

        val token = req.cookies()[authCookiesKey]?.run { this[0]?.value }

        val filter: Mono<ServerResponse> = next(req).flatMap { resp ->
            AuthHolder.getUserInfo()
                    .flatMap { AuthHolder.getToken() }
                    .map { ServerResponseWithAuthCookies(resp, it, auth.getPath()) }
                    .defaultIfEmpty(ServerResponseWithAuthCookies(resp, "", auth.getPath()))
        }

        AuthHolder.initContext(filter, auth, token)
    }
}
