package com.yada.web.repository

import com.yada.web.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.springframework.data.mongodb.core.query.Query as MonoQuery

interface IUserRepository {
    fun changePwd(id: String, pwd: String): Mono<Void>
}

class UserRepositoryImpl @Autowired constructor(
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : IUserRepository {
    override fun changePwd(id: String, pwd: String): Mono<Void> {
        val query = MonoQuery(Criteria.where("id").`is`(id))
        val update = Update().set("pwd", pwd)
        return reactiveMongoTemplate.updateFirst(query, update, User::class.java).then(Mono.empty())
    }
}

interface OrgRepository : ReactiveCrudRepository<Org, String> {
    fun findByIdStartingWithOrderByIdAsc(regex: String): Flux<Org>
}

interface UserRepository : IUserRepository, ReactiveCrudRepository<User, String> {
    fun findByOrgIdOrderByIdAsc(orgId: String): Flux<User>

    @Query("{'id': ?0}", fields = "{'pwd': 1, '_id': 0}")
    fun findPwdById(id: String): Mono<String>

    fun deleteByOrgId(orgId: String): Mono<Void>
}

interface SvcRepository : ReactiveCrudRepository<Svc, String> {
    fun findAllByOrderByIdAsc(): Flux<Svc>
}

interface RoleRepository : ReactiveCrudRepository<Role, String> {
    fun findAllByOrderByIdAsc(): Flux<Role>
}

interface DefaultRoleSvcResRepository : ReactiveCrudRepository<DefaultRoleSvcRes, String>

data class AdminUser(val id: String, val pwd: String)

interface IAdminUserRepository {
    fun changePwd(pwd: String): Mono<Void>
    fun getAdminUser(): Mono<AdminUser>
}

@Component
class AdminUserRepositoryImpl @Autowired constructor(
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : IAdminUserRepository {
    private val collectionName = "admin"

    override fun changePwd(pwd: String): Mono<Void> {
        return reactiveMongoTemplate.save(AdminUser("admin", pwd), collectionName).then()
    }

    override fun getAdminUser(): Mono<AdminUser> {
        val query = MonoQuery(Criteria.where("id").`is`("admin"))
        return reactiveMongoTemplate.findOne(query, collectionName)
    }

}
