package com.yada.services

import com.yada.model.App
import com.yada.repository.AppRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IAppService {
    fun getAll(): Flux<App>
    fun get(id: String): Mono<App>
    fun createOrUpdate(app: App): Mono<App>
    fun exist(id: String): Mono<Boolean>
}

@Service
class AppService constructor(private val appRepository: AppRepository) : IAppService {
    override fun getAll(): Flux<App> = appRepository.findAll()

    override fun get(id: String): Mono<App> = appRepository.findById(id)

    override fun createOrUpdate(app: App): Mono<App> = appRepository.save(app)

    override fun exist(id: String): Mono<Boolean> = appRepository.existsById(id)
}