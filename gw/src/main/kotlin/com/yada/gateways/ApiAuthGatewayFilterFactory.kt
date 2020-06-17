package com.yada.gateways

import com.yada.sc2.AuthHolder
import com.yada.sc2.Operator
import com.yada.sc2.Power
import com.yada.sc2.web.FilterContextBuilder
import com.yada.web.pathPatternParser
import com.yada.web.security.GeneralAuth
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.server.PathContainer
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

class ApiAuthGatewayFilterFactory (
        private val auth: GeneralAuth
) : AbstractGatewayFilterFactory<ApiAuthGatewayFilterFactory.Config>(Config::class.java) {

    override fun shortcutFieldOrder(): MutableList<String> = mutableListOf("checkPower")

    override fun apply(config: Config): GatewayFilter = FilterContextBuilder.buildGatewayFilter(auth, "ApiAuth") { exchange, chain ->
        if (!exchange.response.isCommitted) {
            val op = convertOp(exchange.request.method!!)
            val svcId = exchange.attributes["svcId"]!! as String
            val pathPrefix = exchange.attributes["pathPrefix"]!! as String
            val resListUri = UriComponentsBuilder.fromPath(pathPrefix)
                    .pathSegment(svcId)
                    .pathSegment("res_list")
                    .encode()
                    .build()
                    .toUriString()
            val subUri = exchange.request.uri.path.removePrefix(pathPrefix)

            if (exchange.request.uri.host == "localhost" && exchange.request.uri.path == resListUri) {
                chain.filter(exchange)
            } else {
                AuthHolder.getUserInfo()
                        .filter {
                            config.checkPower == "checkPower" && hasPower(it.powers, op, subUri) || config.checkPower != "checkPower"
                        }.flatMap {
                            val req = exchange.request.mutate()
                                    .header("X-YADA-ORG-ID", it.orgId)
                                    .header("X-YADA-USER-ID", it.userId)
                                    .header("COOKIE", null)
                                    .build()
                            chain.filter(exchange.mutate().request(req).build())
                        }
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED")))

            }
        } else {
            Mono.empty()
        }
    }

    private fun hasPower(resList: List<Power>, op: Operator, uri: String): Boolean = resList.any {
        pathPatternParser.parse(it.res).matches(PathContainer.parsePath(uri)) && op in it.opts
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

    class Config {
        var checkPower: String = "checkPower"
    }

}