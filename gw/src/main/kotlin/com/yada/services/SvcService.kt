package com.yada.services

import com.yada.model.Svc
import com.yada.repository.SvcRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ISvcService {
    fun getAll(): Flux<Svc>
    fun get(id: String): Mono<Svc>
    fun createOrUpdate(svc: Svc): Mono<Svc>
    fun changeId(oldId: String, newId: String): Mono<Svc>
    fun delete(id: String): Mono<Void>
}

@Service
open class SvcService @Autowired constructor(private val repo: SvcRepository, private val appSvc: IAppService) : ISvcService {
    override fun getAll(): Flux<Svc> = repo.findAllByOrderByIdAsc()

    override fun get(id: String): Mono<Svc> = repo.findById(id)

    @Transactional
    override fun createOrUpdate(svc: Svc): Mono<Svc> = repo.save(svc)

    @Transactional
    override fun changeId(oldId: String, newId: String): Mono<Svc> =
            repo.findById(oldId).flatMap { repo.save(it.copy(id = newId)) }.flatMap { repo.deleteById(oldId).then(Mono.just(it)) }

    @Transactional
    override fun delete(id: String): Mono<Void> = appSvc.getAll().flatMap {app ->
        val set = app.resources.filter { it.id != id }.toSet()
        if(set.size != app.resources.size) appSvc.createOrUpdate(app.copy(resources = set)) else Mono.empty()
    }.then(repo.deleteById(id))
}
