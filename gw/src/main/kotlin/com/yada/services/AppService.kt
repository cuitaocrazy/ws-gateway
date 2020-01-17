package com.yada.services

import com.yada.model.App
import com.yada.model.Operator
import com.yada.model.Res
import com.yada.model.SvcRes
import com.yada.repository.AppRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IAppService {
    fun getAll(): Flux<App>
    fun get(id: String): Mono<App>
    fun createOrUpdate(app: App): Mono<App>
    fun exist(id: String): Mono<Boolean>
    fun delete(id: String): Mono<Void>
}

@Service
class AppService constructor(private val appRepository: AppRepository) : IAppService {
    override fun getAll(): Flux<App> = appRepository.findAllByOrderByIdAsc()

    override fun get(id: String): Mono<App> = appRepository.findById(id)

    override fun createOrUpdate(app: App): Mono<App> = extractApp(app).run { appRepository.save(app) }

    override fun exist(id: String): Mono<Boolean> = appRepository.existsById(id)

    override fun delete(id: String): Mono<Void> = appRepository.deleteById(id)

    private val extractApp: (App) -> App = { app ->
        val rs = app.roles.map { r -> r.copy(resources = extractSvcReses(r.resources, app.resources)) }
        app.copy(roles = rs.toSet())
    }
    private val extractSvcReses: (Set<SvcRes>, Set<SvcRes>) -> Set<SvcRes> = { svcReses1, svcReses2 ->
        svcReses1.fold(setOf()) { s, e ->
            val svcRes = svcReses2.firstOrNull { it.id == e.id }
            if (svcRes == null) s else {
                val newSvcRes = extractSvcRes(e, svcRes)
                if(newSvcRes.resources.isEmpty()) s else s + newSvcRes
            }
        }
    }
    private val extractSvcRes: (SvcRes, SvcRes) -> SvcRes = { svcRes1, svcRes2 ->
        val newReses = svcRes1.resources.fold(setOf<Res>()) { s, e ->
            val tRes = svcRes2.resources.firstOrNull { e.uri == it.uri }
            if (tRes == null) s else {
                val newRes = extractRes(e, tRes)
                if(newRes.ops.isEmpty()) s else s + newRes
            }
        }
        svcRes1.copy(resources = newReses)
    }
    private val extractRes: (Res, Res) -> Res = { res1, res2 ->
        val newOpts = res1.ops.fold(setOf<Operator>()) { s, e ->
            if (e in res2.ops) s + e else s
        }
        res1.copy(ops = newOpts)
    }
}
