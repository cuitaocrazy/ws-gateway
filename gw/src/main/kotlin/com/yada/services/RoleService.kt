package com.yada.services

import reactor.core.publisher.Mono

interface IRoleService {
    fun create(appId: String, name: String): Mono<String?>
    fun changeName(id: String): Mono<Void>

}

abstract class RoleService : IRoleService {

}