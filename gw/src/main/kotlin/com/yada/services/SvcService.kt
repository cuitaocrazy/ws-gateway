package com.yada.services

import com.yada.model.Res
import com.yada.model.Svc
import com.yada.model.SvcRes
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ISvcService {
    fun getAllIds(): Flux<String>
    fun get(id: String): Mono<Svc?>
    fun createOrUpdate(svc: Svc): Mono<Svc?>
    fun changeId(oldId: String, newId: String): Mono<Void>
    fun delete(id: String): Mono<Void>
}

class SvrService {
}