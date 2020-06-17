package com.yada.web.services

import com.yada.web.model.Org
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class OrgTree(val org: Org, val children: Set<OrgTree>?)

interface IOrgService {
    fun getTree(orgIdPrefix: String?): Flux<OrgTree>
    fun createOrUpdate(org: Org): Mono<Org>
    fun delete(id: String): Mono<Void>
    fun get(id: String): Mono<Org>
    fun exist(id: String): Mono<Boolean>
}


