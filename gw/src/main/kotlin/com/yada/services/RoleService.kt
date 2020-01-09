package com.yada.services

import com.yada.model.ResWithSvc
import com.yada.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IRoleService {
    fun create(appId: String, id: String): Mono<String?>
    fun get(appId: String, id: String): Mono<Role?>
    fun delete(appId: String, id: String): Mono<Void>
    fun getRoleIds(appId: String): Flux<String>
    fun exist(appId: String, id: String): Mono<Boolean>
    fun updateResources(appId: String, id: String, resources: Set<ResWithSvc>): Mono<Void>
}

abstract class RoleService : IRoleService {

}