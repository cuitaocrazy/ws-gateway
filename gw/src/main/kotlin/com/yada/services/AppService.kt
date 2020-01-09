package com.yada.services

import com.yada.model.App
import com.yada.model.ResWithSvc
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IAppService {
    fun getAllIds(): Flux<String>
    fun get(id: String): Mono<App?>
    fun create(id: String): Mono<App?>
    fun delete(id: String): Mono<Void>
    fun updateResources(id: String, resources: Set<ResWithSvc>): Mono<Void>
    fun changeId(oldId: String, newId: String): Mono<Void>
    fun exist(id: String): Mono<Boolean>
}

class AppService {
}