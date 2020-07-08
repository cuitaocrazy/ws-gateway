package com.yada.web.services

import com.yada.security.Power
import com.yada.web.model.Res
import com.yada.web.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IUserService {
    fun get(id: String): Mono<User>
    fun getByOrgId(orgId: String): Flux<User>
    fun createOrUpdate(user: User): Mono<User>
    fun delete(id: String): Mono<Void>
    fun deleteByOrgId(orgId: String): Mono<Void>
    fun exist(id: String): Mono<Boolean>
    fun getPwd(id: String): Mono<String>
    fun resetPwd(id: String): Mono<Void>
    fun changePwd(id: String, pwd: String): Mono<Void>
    fun changePwd(id: String, oldPwd: String, newPwd: String): Mono<Boolean>
    fun getAll(): Flux<User>
    fun filterApis(apiList: List<Res>, userResList: List<Power>): Mono<List<Res>>
}

