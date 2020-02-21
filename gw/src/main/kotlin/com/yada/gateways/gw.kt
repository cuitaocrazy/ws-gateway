package com.yada.gateways

import com.yada.JwtTokenUtil
import com.yada.token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono
import java.util.function.Predicate

//@Configuration
//open class AuthGateway {
//    @Bean
//    open fun authGatewayRouter(builder: RouteLocatorBuilder): RouteLocator {
//        TODO()
//    }
//}

private val pathPatternParser = PathPatternParser()

@Component
class AppRoutePredicateFactory : AbstractRoutePredicateFactory<AppRoutePredicateFactory.Config>(Config::class.java) {

    override fun shortcutFieldOrder(): MutableList<String> = mutableListOf("subUri", "appName")

    override fun apply(config: Config): Predicate<ServerWebExchange> {
        val uri = UriComponentsBuilder.fromPath(config.subUri).path(config.appName).encode().build().toUriString()
        val pathPattern = pathPatternParser.parse("$uri/**")

        return GatewayPredicate {
            val path = PathContainer.parsePath(it.request.uri.rawPath)
            if (pathPattern.matches(path)) {
                it.attributes["index"] = uri
                true
            } else false
        }
    }

    class Config {
        var subUri: String = ""
        var appName: String = ""

        override fun toString(): String {
            return "subUri=${subUri}, appName=${appName}"
        }
    }
}

@Component
class AuthGatewayFilterFactory @Autowired constructor(private val jwtTokenUtil: JwtTokenUtil)
    : AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config>(Config::class.java) {

    /**
     * 一个配置可能出现两次初始化，是两个线程同时执行的，可能是spring的问题，因此在这个函数里不能做副作用操作，以免出现不可预测的错误
     */
    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val authInfo = exchange.token?.run {
                jwtTokenUtil.getEntity(this)
            }

            if (exchange.request.uri.path == exchange.attributes["index"] && !exchange.response.isCommitted) {
                if (authInfo == null) {
                    val res = exchange.response
                    res.statusCode = HttpStatus.SEE_OTHER
                    res.headers.set(HttpHeaders.LOCATION, getLoginPath(exchange))
                    exchange.response.setComplete()
                } else {
                    val req = exchange.request.mutate()
                            .header("X-YADA-ORG-ID", authInfo.user?.orgId)
                            .header("X-YADA-USER-ID", authInfo.user?.id)
                            .header("COOKIE", null)
                            .build()
                    chain.filter(exchange.mutate().request(req).build())
                }
            } else {
                if (exchange.response.isCommitted) Mono.empty() else chain.filter(exchange)
            }

        }
    }

    private fun getLoginPath(exchange: ServerWebExchange) = UriComponentsBuilder.fromPath("/login").queryParam("redirect", exchange.request.uri.path).build().encode().toUri().toString()
    class Config
}

