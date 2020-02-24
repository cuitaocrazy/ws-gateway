package com.yada.gateways

import com.yada.JwtTokenUtil
import com.yada.model.Operator
import com.yada.model.Res
import com.yada.token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono
import java.util.function.Predicate

private val pathPatternParser = PathPatternParser()

@Component
class AppRoutePredicateFactory : AbstractRoutePredicateFactory<AppRoutePredicateFactory.Config>(Config::class.java) {

    override fun shortcutFieldOrder(): MutableList<String> = mutableListOf("path")

    override fun apply(config: Config): Predicate<ServerWebExchange> {
        val pathPrefix = config.path
        val pathPattern = pathPatternParser.parse("$pathPrefix/**")

        return GatewayPredicate {
            val path = PathContainer.parsePath(it.request.uri.rawPath)
            if (pathPattern.matches(path)) {
                it.attributes["index"] = pathPrefix
                true
            } else false
        }
    }

    class Config {
        var path: String = ""
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

            if ((exchange.request.uri.path == exchange.attributes["index"] || exchange.request.uri.path == (exchange.attributes["index"] as String + "/")) && !exchange.response.isCommitted) {
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

@Component
class SvcRoutePredicateFactory : AbstractRoutePredicateFactory<SvcRoutePredicateFactory.Config>(Config::class.java) {

    override fun shortcutFieldOrder(): MutableList<String> = mutableListOf("pathPrefix", "svcId")

    override fun apply(config: Config): Predicate<ServerWebExchange> {
        val pathPrefix = UriComponentsBuilder.fromPath(config.pathPrefix).pathSegment(config.svcId).encode().build().toUriString()
        val pathPattern = pathPatternParser.parse("$pathPrefix/**")

        return GatewayPredicate {
            val path = PathContainer.parsePath(it.request.uri.rawPath)
            if (pathPattern.matches(path)) {
                it.attributes["pathPrefix"] = config.pathPrefix
                it.attributes["svcId"] = config.svcId
                true
            } else false
        }
    }

    class Config {
        var pathPrefix: String = ""
        var svcId: String = ""
    }
}

@Component
class AuthApiGatewayFilterFactory @Autowired constructor(private val jwtTokenUtil: JwtTokenUtil)
    : AbstractGatewayFilterFactory<AuthApiGatewayFilterFactory.Config>(Config::class.java) {

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val authInfo = exchange.token?.run {
                jwtTokenUtil.getEntity(this)
            }

            if (!exchange.response.isCommitted) {
                val op = convertOp(exchange.request.method!!)
                val svcId = exchange.attributes["svcId"]!! as String
                val pathPrefix = exchange.attributes["pathPrefix"]!! as String
                val resListUri = UriComponentsBuilder.fromPath(pathPrefix).pathSegment(svcId).pathSegment("res_list").encode().build().toUriString()
                val subUri = exchange.request.uri.path.removePrefix(pathPrefix)

                if (exchange.request.uri.host == "localhost" && exchange.request.uri.path == resListUri) {
                    chain.filter(exchange)
                } else if (authInfo == null || !hasPower(authInfo.resList!!, op, subUri)) {
                    Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"))
                } else {
                    val req = exchange.request.mutate()
                            .header("X-YADA-ORG-ID", authInfo.user?.orgId)
                            .header("X-YADA-USER-ID", authInfo.user?.id)
                            .header("COOKIE", null)
                            .build()
                    chain.filter(exchange.mutate().request(req).build())
                }
            } else {
                Mono.empty()
            }

        }
    }

    private fun hasPower(resList: List<Res>, op: Operator, uri: String): Boolean = resList.any {
        pathPatternParser.parse(it.uri).matches(PathContainer.parsePath(uri)) && op in it.ops
    }

    private fun convertOp(method: HttpMethod): Operator = when (method) {
        HttpMethod.GET -> Operator.READ
        HttpMethod.OPTIONS -> Operator.READ
        HttpMethod.TRACE -> Operator.READ
        HttpMethod.POST -> Operator.CREATE
        HttpMethod.PUT -> Operator.UPDATE
        HttpMethod.PATCH -> Operator.UPDATE
        HttpMethod.DELETE -> Operator.DELETE
        else -> throw Error("不支持${method}")
    }

    class Config
}