package com.yada.services

import com.yada.model.Res
import com.yada.model.Svc
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ISvcService {
    fun getAllIds(): Flux<String>
    fun get(id: String): Mono<Svc?>
    fun create(id: String, resources: Set<Res>): Mono<Svc?>
    fun changeId(oldId: String, newId: String): Mono<Void>
    fun changeRes(id: String, resources: Set<Res>): Mono<Void>
    fun delete(id: String): Mono<Void>
}

class SvrService {
}