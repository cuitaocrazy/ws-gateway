package com.yada.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.yada.model.App
import com.yada.repository.AppRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IAppService {
    fun getAllIds(): Flux<String>
    fun get(id: String): Mono<App>
    fun createOrUpdate(app: App): Mono<App>
    fun exist(id: String): Mono<Boolean>
}

@Service
class AppService constructor(private val appRepository: AppRepository) : IAppService {
    override fun getAllIds(): Flux<String> = appRepository.findAllIds().map { ObjectMapper().readTree(it)["_id"]?.asText() }

    override fun get(id: String): Mono<App> = appRepository.findById(id)

    override fun createOrUpdate(app: App): Mono<App> = appRepository.save(app)

    override fun exist(id: String): Mono<Boolean> = appRepository.existsById(id)
}