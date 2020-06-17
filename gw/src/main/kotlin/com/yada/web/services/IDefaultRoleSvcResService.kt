package com.yada.web.services

import com.yada.web.model.DefaultRoleSvcRes
import reactor.core.publisher.Mono

interface IDefaultRoleSvcResService {
    fun get(): Mono<List<DefaultRoleSvcRes>>
    fun createOrUpdate(defaultRoleSvcRes: List<DefaultRoleSvcRes>): Mono<List<DefaultRoleSvcRes>>
}

