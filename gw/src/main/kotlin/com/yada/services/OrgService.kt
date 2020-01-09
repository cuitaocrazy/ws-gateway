package com.yada.services

import com.yada.model.Org
import com.yada.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class OrgTree(val org: Org, val children: Set<Org>?)

interface IOrgService {
    fun getTree(orgId: String?): Mono<OrgTree>
    fun getChildren(orgId: String?): Flux<Org>
    fun getUserIds(orgId: String): Flux<String>
    fun create(id: String, name: String): Mono<Org?>
    fun changeName(id: String, name: String): Mono<Org?>
    fun delete(id: String): Mono<Void>
    fun get(id: String): Mono<Org?>
    fun exist(id: String): Mono<Boolean>
}

abstract class OrgService : IOrgService {

}