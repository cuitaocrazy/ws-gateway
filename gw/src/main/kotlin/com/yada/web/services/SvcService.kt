package com.yada.web.services

import com.yada.LoggerDelegate
import com.yada.gateways.SvcRoutePredicateFactory
import com.yada.web.model.Res
import com.yada.web.model.Svc
import com.yada.web.repository.SvcRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.config.GatewayProperties
import org.springframework.cloud.gateway.support.ConfigurationService
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ISvcService {
    fun getAll(): Flux<Svc>
    fun get(id: String): Mono<Svc>
    fun createOrUpdate(svc: Svc): Mono<Svc>
    fun changeId(oldId: String, newId: String): Mono<Svc>
    fun delete(id: String): Mono<Void>
    fun getReallyRes(svcId: String): Flux<Res>
}

@Service
open class SvcService @Autowired constructor(
        private val repo: SvcRepository,
        private val roleSvc: IRoleService,
        private val context: ApplicationContext
) : ISvcService {
    val log by LoggerDelegate()

    override fun getAll(): Flux<Svc> = repo.findAllByOrderByIdAsc()

    override fun get(id: String): Mono<Svc> = repo.findById(id)

    @Transactional
    override fun createOrUpdate(svc: Svc): Mono<Svc> = repo.save(svc)

    @Transactional
    override fun changeId(oldId: String, newId: String): Mono<Svc> =
            repo.findById(oldId).flatMap { repo.save(it.copy(id = newId)) }.flatMap { repo.deleteById(oldId).then(Mono.just(it)) }

    @Transactional
    override fun delete(id: String): Mono<Void> = roleSvc.getAll().flatMap { role ->
        val set = role.svcs.filter { it.id != id }.toSet()
        if (set.size != role.svcs.size) roleSvc.createOrUpdate(role.copy(svcs = set)) else Mono.empty()
    }.then(repo.deleteById(id))

    override fun getReallyRes(svcId: String): Flux<Res> = getSvcServerUrl(svcId).flatMapIterable { it }

    private fun getSvcServerUrl(svcId: String): Mono<List<Res>> {
        val properties = context.getBean(GatewayProperties::class.java)
        val env = context.getBean(Environment::class.java)
        val svcRoutePredicateFactory = context.getBean(SvcRoutePredicateFactory::class.java)
        val configurationService = context.getBean(ConfigurationService::class.java)

        val list = properties.routes.mapNotNull { routeDefinition ->
            val appPredicate = routeDefinition.predicates.firstOrNull { it.name == "Svc" }
            if (appPredicate != null) {
                // org.springframework.boot.web.serve.AbstractConfigurableWebServerFactory默认端口：8080
                val port = env.getProperty("server.port") ?: "8080"
                val schema = if (env.getProperty("server.ssl.key-store") == null) "http" else "https"
                val config = configurationService.with(svcRoutePredicateFactory).name(appPredicate.name).properties(appPredicate.args).bind() as SvcRoutePredicateFactory.Config
                Pair(config.svcId, "$schema://localhost:$port" + UriComponentsBuilder.fromPath(config.pathPrefix).pathSegment(config.svcId).pathSegment("res_list").encode().build().toUriString())
            } else null
        }
        val p = list.firstOrNull{
            item ->
            item.first == svcId
        }

        return if(p != null) {
            WebClient.create(p.second).get().accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono<List<Res>>().onErrorResume{
                err ->
                log.error(err.message)
                Mono.just(listOf())
            }
        } else {
            Mono.empty()
        }
    }
}
