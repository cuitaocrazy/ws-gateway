package com.yada.services

import com.yada.model.Role
import com.yada.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IUserService {
    fun get(id: String): Mono<User?>
    fun getPwd(id: String): Mono<String?>
    fun create(id: String, pwd: String): Mono<User?>
    fun changePwd(id: String, pwd: String): Mono<Void>
    fun delete(id: String): Mono<Void>
    fun getRoleIds(id: String): Flux<Role>
    fun addRole(id: String, roleId: String): Mono<Void>
    fun removeRole(id: String, roleId: String): Mono<Void>
}

abstract class UserService : IUserService {
}