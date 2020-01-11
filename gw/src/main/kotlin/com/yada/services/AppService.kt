package com.yada.services

import com.yada.model.App
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IAppService {
    fun getAllIds(): Flux<String>
    fun get(id: String): Mono<App?>
    fun createOrUpdate(app: App): Mono<App?>
    fun exist(id: String): Mono<Boolean>
}

class AppService {
}