package com.yada

import com.yada.security.AuthInfo
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

typealias Next = (ServerRequest) -> Mono<ServerResponse>
typealias Filter = (request: ServerRequest, next: Next) -> Mono<ServerResponse>
typealias Verify = (authInfo: AuthInfo?) -> Boolean
typealias Unauth = Filter