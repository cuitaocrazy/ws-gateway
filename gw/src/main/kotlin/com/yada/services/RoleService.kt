package com.yada.services

import com.yada.model.Role
import com.yada.model.RoleId
import com.yada.model.SvcRes
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IRoleService {
    fun create(id: RoleId): Mono<String?>
    fun get(id: RoleId): Mono<Role?>
    fun delete(id: RoleId): Mono<Void>
    fun getRoleIds(appId: String): Flux<String>
    fun exist(id: RoleId): Mono<Boolean>
    fun updateResources(id: RoleId, resources: Set<SvcRes>): Mono<Void>
}

abstract class RoleService : IRoleService {

}