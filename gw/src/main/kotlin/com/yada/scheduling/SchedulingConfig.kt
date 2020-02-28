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

/**
 *
 * # service资源列表更新任务
 *
 * ## 关于动态更新路由的一些问题(目前不支持)
 *
 * 目前只能注入[ConfigurationService]来准确的获取svcId，不能从[RouteLocator][org.springframework.cloud.gateway.route.RouteLocator]
 * 读取[SvcRoutePredicateFactory.Config], 因此就算写一个[ApplicationListener][org.springframework.context.ApplicationListener]去监听
 * [RefreshRoutesEvent][org.springframework.cloud.gateway.event.RefreshRoutesEvent], 也不能读取[SvcRoutePredicateFactory.Config]，
 *
 * 参考[GatewayControllerEndpoint.routes][org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint.routes]方法，它现在
 * 也没实现这个问题，但是他有TODO等待完成，因此等待它去完善这个再去实现动态更新。
 *
 */
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
        val list = properties.routes.mapNotNull { routeDefinition ->
            val appPredicate = routeDefinition.predicates.filter { it.name == "Svc" }.firstOrNull()
            if (appPredicate != null) {
                // org.springframework.boot.web.serve.AbstractConfigurableWebServerFactory默认端口：8080
                val port = env.getProperty("server.port") ?: "8080"
                val schema = if (env.getProperty("server.ssl.key-store") == null) "http" else "https"
                val config = configurationService.with(svcRoutePredicateFactory).name(appPredicate.name).properties(appPredicate.args).bind() as SvcRoutePredicateFactory.Config
                Pair(config.svcId, "$schema://localhost:$port" + UriComponentsBuilder.fromPath(config.pathPrefix).pathSegment(config.svcId).pathSegment("res_list").encode().build().toUriString())
            } else null
        }
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