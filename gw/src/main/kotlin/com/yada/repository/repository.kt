package com.yada.repository

import com.yada.model.App
import com.yada.model.Org
import com.yada.model.Svc
import com.yada.model.User
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrgRepository : ReactiveCrudRepository<Org, String> {
    @Query("{'id': { \$regex: ?0 }}", sort = "{'id': 1}")
    fun findByRegexId(regex: String): Flux<Org>
}

interface UserRepository : ReactiveCrudRepository<User, String> {
    @Query("{'orgId': ?0}")
    fun findByOrgId(orgId: String): Flux<User>
    @Query("{'id': ?0}", fields = "{'pwd': 1, '_id': 0}")
    fun fundOnPwd(id: String): Mono<String>
}

interface SvcRepository : ReactiveCrudRepository<Svc, String> {
    @Query("{}", sort ="{'id': 1}", fields = "{'id': 1}")
    fun findAllIds(): Flux<String>
}

interface  AppRepository : ReactiveCrudRepository<App, String> {
    @Query("{}", sort ="{'id': 1}", fields = "{'id': 1}")
    fun findAllIds(): Flux<String>
}