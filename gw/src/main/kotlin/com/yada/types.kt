package com.yada

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

typealias Next = (ServerRequest) -> Mono<ServerResponse>
typealias Filter = (request: ServerRequest, next: Next) -> Mono<ServerResponse>