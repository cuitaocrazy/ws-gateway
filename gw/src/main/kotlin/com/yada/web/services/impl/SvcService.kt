package com.yada.web.services.impl

import com.yada.LoggerDelegate
import com.yada.web.model.Svc
import com.yada.web.repository.SvcRepository
import com.yada.web.services.ISvcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
open class SvcService @Autowired constructor(
        private val repo: SvcRepository
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
    override fun delete(id: String): Mono<Void> = repo.deleteById(id)
}