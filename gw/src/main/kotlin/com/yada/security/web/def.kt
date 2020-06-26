package com.yada.security.web

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

const val authCookiesKey = "AUTH_ID"

typealias GatewayFilterHandle = (exchange: ServerWebExchange, chain: GatewayFilterChain) -> Mono<Void>

typealias FluxFilterFunction = (ServerRequest, (ServerRequest) -> Mono<ServerResponse>) -> Mono<ServerResponse>
