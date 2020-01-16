package com.yada.services

import com.yada.model.Svc
import com.yada.repository.SvcRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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
class SvcService @Autowired constructor(private val repo: SvcRepository) : ISvcService {
    override fun getAll(): Flux<Svc> = repo.findAllByOrderByIdAsc()

    override fun get(id: String): Mono<Svc> = repo.findById(id)

    override fun createOrUpdate(svc: Svc): Mono<Svc> = repo.save(svc)

    override fun changeId(oldId: String, newId: String): Mono<Svc> =
            repo.findById(oldId).flatMap { repo.save(it.copy(id = newId)) }.flatMap { repo.deleteById(oldId).then(Mono.just(it)) }

    override fun delete(id: String): Mono<Void> = repo.deleteById(id)
}
