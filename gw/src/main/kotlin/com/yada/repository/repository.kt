package com.yada.repository

import com.yada.model.App
import com.yada.model.Org
import com.yada.model.Svc
import com.yada.model.User
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrgRepository : ReactiveCrudRepository<Org, String> {
    @Query("{'id': { \$regex: ?0 }}", sort = "{'id': 1}")
    fun findByRegexId(regex: String): Flux<Org>
}

interface UserRepository : ReactiveCrudRepository<User, String>

interface SvcRepository : ReactiveCrudRepository<Svc, String>

interface  AppRepository : ReactiveCrudRepository<App, String>