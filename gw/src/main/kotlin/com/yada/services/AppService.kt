package com.yada.services

import com.yada.model.App
import com.yada.model.Res
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IAppService {
    fun getAllIds(): Flux<String>
    fun get(id: String): Mono<App?>
    fun create(id: String): Mono<App?>
    fun delete(id: String): Mono<Void>
    fun addRes(id: String, resources: Set<Res>): Mono<Void>
    fun removeRes(id: String, resources: Set<Res>): Mono<Void>
    fun changeId(oldId: String, newId: String): Mono<Void>
    fun exist(id: String): Mono<Boolean>
    fun getRoleIds(id: String): Flux<String>
}

class AppService {
}