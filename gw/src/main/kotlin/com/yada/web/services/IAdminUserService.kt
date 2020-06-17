package com.yada.web.services

import reactor.core.publisher.Mono

interface IAdminUserService {
    fun changePwd(id: String, oldPwd: String, newPwd: String): Mono<Boolean>
}

