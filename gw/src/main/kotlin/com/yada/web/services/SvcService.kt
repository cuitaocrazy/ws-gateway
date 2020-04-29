package com.yada.web.services

import com.yada.LoggerDelegate
import com.yada.web.model.Svc
import com.yada.web.repository.SvcRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

interface ISvcService {
    fun getAll(): Mono<List<Svc>>
    fun get(id: String): Mono<Svc>
    fun createOrUpdate(svc: Svc): Mono<Svc>
    fun changeId(oldId: String, newId: String): Mono<Svc>
    fun delete(id: String): Mono<Void>
}

@Service
open class SvcService @Autowired constructor(
        private val repo: SvcRepository,
        private val roleSvc: IRoleService
) : ISvcService {
    val log by LoggerDelegate()

    override fun getAll(): Mono<List<Svc>> = repo.findAllByOrderByIdAsc().collectList()

    override fun get(id: String): Mono<Svc> = repo.findById(id)

    @Transactional
    override fun createOrUpdate(svc: Svc): Mono<Svc> = repo.save(svc)

    @Transactional
    override fun changeId(oldId: String, newId: String): Mono<Svc> =
            repo.findById(oldId)
                    .flatMap { repo.save(it.copy(id = newId)) }
                    .flatMap { repo.deleteById(oldId).then(Mono.just(it)) }

    @Transactional
    override fun delete(id: String): Mono<Void> =
            roleSvc.getAll()
                    .flatMapIterable { it }
                    .flatMap { role ->
                        val set = role.svcs.filter { it.id != id }.toSet()
                        if (set.size != role.svcs.size)
                            roleSvc.createOrUpdate(role.copy(svcs = set))
                        else
                            Mono.empty()
                    }.then(repo.deleteById(id))
}
