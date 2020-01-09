package com.yada.services

import com.yada.model.Org
import com.yada.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class OrgTree(val org: Org, val children: Set<Org>?)

interface IOrgService {
    fun getOrgTree(orgId: String?): Mono<OrgTree>
    fun getOrgChildren(orgId: String?): Flux<Org>
    fun getUserIds(orgId: String): Flux<String>
    fun create(id: String, name: String): Mono<Org?>
    fun changeName(id: String, name: String): Mono<Org?>
    fun delete(id: String): Mono<Void>
    fun get(id: String): Mono<Org?>
}

abstract class OrgService : IOrgService {

}