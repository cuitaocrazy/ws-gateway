package com.yada.gateways

import com.yada.web.pathPatternParser
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import java.util.function.Predicate

@Component
class SvcRoutePredicateFactory : AbstractRoutePredicateFactory<SvcRoutePredicateFactory.Config>(Config::class.java) {

    override fun shortcutFieldOrder(): MutableList<String> = mutableListOf("pathPrefix", "svcId")

    override fun apply(config: Config): Predicate<ServerWebExchange> {
        val pathPrefix = UriComponentsBuilder.fromPath(config.pathPrefix)
                .pathSegment(config.svcId)
                .encode()
                .build()
                .toUriString()
        val pathPattern = pathPatternParser.parse("$pathPrefix/**")

        return object : GatewayPredicate {
            override fun test(exchange: ServerWebExchange): Boolean {
                val path = PathContainer.parsePath(exchange.request.uri.rawPath)
                return if (pathPattern.matches(path)) {
                    exchange.attributes["pathPrefix"] = config.pathPrefix
                    exchange.attributes["svcId"] = config.svcId
                    true
                } else
                    false
            }

            override fun toString(): String {
                return "Svc: pathPrefix=${config.pathPrefix}, svcId=${config.svcId}"
            }
        }
    }

    class Config {
        var pathPrefix: String = ""
        var svcId: String = ""
    }
}