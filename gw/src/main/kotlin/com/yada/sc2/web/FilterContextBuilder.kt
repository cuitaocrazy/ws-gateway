package com.yada.sc2.web

import com.yada.sc2.Auth
import com.yada.sc2.AuthHolder
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.ResponseCookie
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

object FilterContextBuilder {
    fun buildGatewayFilter(auth: Auth, name: String, handle: GatewayFilterHandle): GatewayFilter = object : GatewayFilter {
        override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void>? {
            val token = exchange.request.cookies[authCookiesKey]?.run { this[0]?.value }

            val setCookies = { _token: String ->
                val cookie = ResponseCookie.from(authCookiesKey, _token).build()
                exchange.response.addCookie(cookie)
            }

            exchange.response.beforeCommit {
                Mono.subscriberContext().flatMap { ctx ->
                    ctx.getOrEmpty<String>(AuthHolder.tokenKey).map { token ->
                        setCookies(token)
                        auth.refreshToken(token)
                    }.orElse(Mono.empty())
                }
            }

            return handle(exchange, chain).subscriberContext { ctx ->
                ctx.apply {
                    put(AuthHolder.authKey, auth)
                    if (token != null) {
                        put(AuthHolder.tokenKey, token)
                    }
                    put(AuthHolder.sendTokenFnKey, setCookies)
                }
            }
        }

        override fun toString(): String {
            return name
        }
    }

    fun buildFluxFilter(auth: Auth, handle: FluxFilterFunction): FluxFilterFunction = { req, next ->
        val token = req.cookies()[authCookiesKey]?.run { this[0]?.value }

        var tmp: String? = token

        val setToken = { _token: String ->
            tmp = _token
        }

        handle(req, next).flatMap {
            if (tmp != null)
                auth.refreshToken(tmp!!).then(Mono.just(ServerResponseWithAuthCookies.from(it, tmp!!)))
            else
                Mono.just(it)
        }.subscriberContext { ctx ->
            ctx.apply {
                put(AuthHolder.authKey, auth)
                if (token != null) {
                    put(AuthHolder.tokenKey, token)
                }
                put(AuthHolder.sendTokenFnKey, setToken)
            }
        }
    }

    fun buildDefaultFluxFilter(auth: Auth): FluxFilterFunction = { req, next ->

        val token = req.cookies()[authCookiesKey]?.run { this[0]?.value }

        var tmp: String? = token

        val setToken = { _token: String ->
            tmp = _token
        }

        next(req).flatMap {
            if (tmp != null)
                auth.refreshToken(tmp!!).then(Mono.just(ServerResponseWithAuthCookies.from(it, tmp!!)))
            else
                Mono.just(it)
        }.subscriberContext { ctx ->
            ctx.put(AuthHolder.authKey, auth)
                    .put(AuthHolder.sendTokenFnKey, setToken).run {
                        if (token != null)
                            put(AuthHolder.tokenKey, token)
                        else
                            this
                    }
        }
    }
}
