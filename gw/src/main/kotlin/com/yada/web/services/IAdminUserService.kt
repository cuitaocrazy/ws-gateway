package com.yada.web.services

import com.yada.web.model.Admin
import reactor.core.publisher.Mono

interface IAdminUserService {
    fun createUser(id: String): Mono<Admin>
    fun deleteUser(id: String): Mono<Void>
    fun changePwd(id: String, oldPwd: String, newPwd: String): Mono<Boolean>
}

