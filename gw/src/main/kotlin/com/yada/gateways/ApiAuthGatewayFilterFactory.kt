package com.yada.gateways

import com.yada.security.JwtTokenUtil
import com.yada.security.token
import com.yada.web.model.Operator
import com.yada.web.model.Res
import com.yada.web.pathPatternParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class ApiAuthGatewayFilterFactory @Autowired constructor(private val jwtTokenUtil: JwtTokenUtil)
    : AbstractGatewayFilterFactory<ApiAuthGatewayFilterFactory.Config>(Config::class.java) {

    override fun shortcutFieldOrder(): MutableList<String> = mutableListOf("checkPower")

    override fun apply(config: Config): GatewayFilter {
        return object : GatewayFilter {
            override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
                val authInfo = exchange.token?.run {
                    jwtTokenUtil.getEntity(this)
                }

                return if (!exchange.response.isCommitted) {
                    val op = convertOp(exchange.request.method!!)
                    val svcId = exchange.attributes["svcId"]!! as String
                    val pathPrefix = exchange.attributes["pathPrefix"]!! as String
                    val resListUri = UriComponentsBuilder.fromPath(pathPrefix).pathSegment(svcId).pathSegment("res_list").encode().build().toUriString()
                    val subUri = exchange.request.uri.path.removePrefix(pathPrefix)

                    if (exchange.request.uri.host == "localhost" && exchange.request.uri.path == resListUri) {
                        chain.filter(exchange)
                    } else if (authInfo == null || (config.checkPower == "checkPower" && !hasPower(authInfo.resList!!, op, subUri))) {
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

            override fun toString(): String {
                return "ApiAuth"
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

    class Config {
        var checkPower: String = "checkPower"
    }
}