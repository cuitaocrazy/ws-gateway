package com.yada.web.services

import com.yada.web.model.Svc
import reactor.core.publisher.Mono

interface ISvcService {
    fun getAll(): Mono<List<Svc>>
    fun get(id: String): Mono<Svc>
    fun createOrUpdate(svc: Svc): Mono<Svc>
    fun changeId(oldId: String, newId: String): Mono<Svc>
    fun delete(id: String): Mono<Void>
}

