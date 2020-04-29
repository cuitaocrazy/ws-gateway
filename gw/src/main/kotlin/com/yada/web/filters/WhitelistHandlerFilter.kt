package com.yada.web.filters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class WhitelistHandlerFilter @Autowired constructor(
        @Value("\${yada.admin.ipWhitelist:}")
        private val whitelist: List<String>
) : Filter {
    override fun invoke(request: ServerRequest, next: Next): Mono<ServerResponse> =
            if (
                    whitelist.isNotEmpty()
                    && request.remoteAddress().isPresent
                    && request.remoteAddress().get().address.hostAddress
                    !in whitelist
            ) {
                Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))
            } else {
                next(request)
            }
}