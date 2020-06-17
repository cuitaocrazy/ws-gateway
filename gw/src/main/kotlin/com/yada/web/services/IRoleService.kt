package com.yada.web.services

import com.yada.web.model.Role
import reactor.core.publisher.Mono

interface IRoleService {
    fun getAll(): Mono<List<Role>>
    fun get(id: String): Mono<Role>
    fun exist(id: String): Mono<Boolean>
    fun createOrUpdate(role: Role): Mono<Role>
    fun delete(id: String): Mono<Void>
}

