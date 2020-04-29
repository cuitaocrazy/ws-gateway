package com.yada.gateways

import com.yada.web.pathPatternParser
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.function.Predicate

@Component
class AppRoutePredicateFactory : AbstractRoutePredicateFactory<AppRoutePredicateFactory.Config>(Config::class.java) {

    override fun shortcutFieldOrder(): MutableList<String> = mutableListOf("path")

    override fun apply(config: Config): Predicate<ServerWebExchange> {
        val pathPrefix = config.path
        val pathPattern = pathPatternParser.parse("$pathPrefix/**")

        return object : GatewayPredicate {
            override fun test(exchange: ServerWebExchange): Boolean {
                val path = PathContainer.parsePath(exchange.request.uri.rawPath)
                return if (pathPattern.matches(path)) {
                    exchange.attributes["index"] = pathPrefix
                    true
                } else false
            }

            override fun toString(): String {
                return "App: path=${config.path}"
            }
        }
    }

    class Config {
        var path: String = ""
    }
}