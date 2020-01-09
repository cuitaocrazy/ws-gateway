package com.yada.services

import com.yada.model.ResWithSvc
import reactor.core.publisher.Mono

interface IRoleService {
    fun create(appId: String, name: String): Mono<String?>
    fun changeName(id: String): Mono<Void>
    fun changeResources(id: String, res: Array<ResWithSvc>): Mono<Void>
    fun delete(id: String): Mono<Void>
}

abstract class RoleService : IRoleService {

}