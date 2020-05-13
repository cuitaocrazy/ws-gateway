package com.yada.gateways

import com.yada.security.AuthInfoParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class AuthGatewayFilterFactory @Autowired constructor(private val authInfoParser: AuthInfoParser)
    : AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config>(Config::class.java) {

    /**
     * 一个配置可能出现两次初始化，是两个线程同时执行的，可能是spring的问题，因此在这个函数里不能做副作用操作，以免出现不可预测的错误
     */
    override fun apply(config: Config): GatewayFilter {
        return object : GatewayFilter {
            override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {

                return if ((exchange.request.uri.path == exchange.attributes["index"] || exchange.request.uri.path == (exchange.attributes["index"] as String + "/")) && !exchange.response.isCommitted) {
                    authInfoParser.getAuthInfo(authInfoParser.getToken(exchange)).flatMap {
                        val req = exchange.request.mutate()
                                .header("X-YADA-ORG-ID", it.user.orgId)
                                .header("X-YADA-USER-ID", it.user.id)
                                .header("COOKIE", null)
                                .build()
                        chain.filter(exchange.mutate().request(req).build())
                    }.switchIfEmpty(run {
                        val res = exchange.response
                        res.statusCode = HttpStatus.SEE_OTHER
                        res.headers.set(HttpHeaders.LOCATION, getLoginPath(exchange))
                        exchange.response.setComplete()
                    })
                } else {
                    if (exchange.response.isCommitted) Mono.empty() else chain.filter(exchange)
                }
            }

            override fun toString(): String {
                return "Auth"
            }
        }
    }

    private fun getLoginPath(exchange: ServerWebExchange) =
            UriComponentsBuilder.fromPath("/login")
                    .queryParam("redirect", exchange.request.uri.path)
                    .build()
                    .encode()
                    .toUriString()

    class Config
}