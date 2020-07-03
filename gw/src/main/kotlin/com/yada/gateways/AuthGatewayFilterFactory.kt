package com.yada.gateways

import com.yada.security.AuthHolder
import com.yada.security.UserInfo
import com.yada.security.web.FilterContextBuilder
import com.yada.web.security.GeneralAuth
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

class AuthGatewayFilterFactory(
        private val auth: GeneralAuth,
        private val contextPath: String
) : AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config>(Config::class.java) {

    /**
     * 一个配置可能出现两次初始化，是两个线程同时执行的，可能是spring的问题，因此在这个函数里不能做副作用操作，以免出现不可预测的错误
     */
    override fun apply(config: Config): GatewayFilter = FilterContextBuilder.buildGatewayFilter(auth, "Auth") { exchange, chain ->
        val isEntryPath = exchange.request.uri.path == exchange.attributes["index"]
                || exchange.request.uri.path == (exchange.attributes["index"] as String + "/")

        if (isEntryPath && !exchange.response.isCommitted) {
            AuthHolder.getUserInfo().switchIfEmpty(Mono.defer {
                val res = exchange.response
                res.statusCode = HttpStatus.SEE_OTHER
                res.headers.set(HttpHeaders.LOCATION, getLoginPath(exchange, contextPath))
                exchange.response.setComplete().then(Mono.empty<UserInfo>())
            }).flatMap {
                val req = exchange.request.mutate()
                        .header("X-YADA-ORG-ID", it.orgId)
                        .header("X-YADA-USER-ID", it.userId)
                        .header("COOKIE", null)
                        .build()
                chain.filter(exchange.mutate().request(req).build())
            }
        } else {
            if (exchange.response.isCommitted) Mono.empty() else chain.filter(exchange)
        }
    }

    private fun getLoginPath(exchange: ServerWebExchange, contextPath: String) =
            UriComponentsBuilder.fromPath("$contextPath/login")
                    .queryParam("redirect", contextPath + exchange.request.uri.path)
                    .build()
                    .encode()
                    .toUriString()

    class Config

}