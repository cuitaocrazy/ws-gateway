package com.yada.repository

import com.yada.model.App
import com.yada.model.Org
import com.yada.model.Svc
import com.yada.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IUserRepository {
    fun changePwd(id: String, pwd: String): Mono<Void>
}

class UserRepositoryImpl @Autowired constructor(private val reactiveMongoTemplate: ReactiveMongoTemplate) : IUserRepository {
    override fun changePwd(id: String, pwd: String): Mono<Void> {
        val query = org.springframework.data.mongodb.core.query.Query(Criteria.where("id").`is`(id))
        val update = Update().set("pwd", pwd)
        return reactiveMongoTemplate.updateFirst(query, update, User::class.java).then(Mono.create<Void> { it.success() })
    }
}

interface OrgRepository : ReactiveCrudRepository<Org, String> {
    @Query("{'id': { \$regex: ?0 }}", sort = "{'id': 1}")
    fun findByRegexId(regex: String): Flux<Org>
}

interface UserRepository : IUserRepository, ReactiveCrudRepository<User, String> {
    @Query("{'orgId': ?0}")
    fun findByOrgId(orgId: String): Flux<User>
    @Query("{'id': ?0}", fields = "{'pwd': 1, '_id': 0}")
    fun fundOnPwd(id: String): Mono<String>
}

interface SvcRepository : ReactiveCrudRepository<Svc, String>

interface  AppRepository : ReactiveCrudRepository<App, String>
