package com.yada.scheduling

import com.yada.gateways.SvcRoutePredicateFactory
import com.yada.model.Res
import com.yada.model.Svc
import com.yada.services.ISvcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration
import org.springframework.cloud.gateway.config.GatewayProperties
import org.springframework.cloud.gateway.support.ConfigurationService
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Configuration
@EnableScheduling
@AutoConfigureBefore(GatewayAutoConfiguration::class)
open class GwConfig @Autowired constructor(
        properties: GatewayProperties,
        svcRoutePredicateFactory: SvcRoutePredicateFactory,
        configurationService: ConfigurationService,
        env: Environment,
        private val svcService: ISvcService) {
    private val svcMap = mutableMapOf<String, String>()

    init {
        val list = properties.routes.map { routeDefinition ->
            val appPredicate = routeDefinition.predicates.filter { it.name == "Svc" }.firstOrNull()
            if (appPredicate != null) {
                // org.springframework.boot.web.serve.AbstractConfigurableWebServerFactory默认端口：8080
                val port = env.getProperty("server.port") ?: "8080"
                val schema = if (env.getProperty("server.ssl.key-store") == null) "http" else "https"
                val config = configurationService.with(svcRoutePredicateFactory).name(appPredicate.name).properties(appPredicate.args).bind() as SvcRoutePredicateFactory.Config
                Pair(config.svcId, "$schema://localhost:$port" + UriComponentsBuilder.fromPath(config.pathPrefix).pathSegment(config.svcId).pathSegment("res_list").encode().build().toUriString())
            } else null
        }.filterNotNull()
        list.forEach {
            svcMap[it.first] = it.second
        }
    }

    @Scheduled(fixedDelay = 1 * 60 * 60 * 1000, initialDelay = 10000)
    fun fetchServiceRes() {
        svcMap.forEach {
            WebClient.create(it.value).get().accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono<List<Res>>().flatMap { resList ->
                svcService.createOrUpdate(Svc(it.key, resList.toSet()))
            }.subscribe()
        }
    }
}