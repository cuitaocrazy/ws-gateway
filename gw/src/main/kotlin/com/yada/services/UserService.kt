package com.yada.services

import com.yada.model.RoleId
import com.yada.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface IUserService {
    fun get(id: String): Mono<User?>
    fun getPwd(id: String): Mono<String?>
    fun create(id: String, pwd: String, orgId: String): Mono<User?>
    fun getUserIds(orgId: String): Flux<String>
    fun changePwd(id: String, pwd: String): Mono<Void>
    fun delete(id: String): Mono<Void>
    fun exist(id: String): Mono<Boolean>

    fun updateRoles(id: String, roleIds: Flux<RoleId>): Mono<Void>
}

abstract class UserService : IUserService {
}