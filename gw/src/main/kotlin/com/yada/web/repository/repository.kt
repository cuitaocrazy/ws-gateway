package com.yada.web.repository

import com.yada.web.model.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.springframework.data.mongodb.core.query.Query as MonoQuery

interface IUserRepository {
    fun changePwd(id: String, pwd: String): Mono<Void>
    fun findPwdById(id: String): Mono<String>
}

class UserRepositoryImpl @Autowired constructor(
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : IUserRepository {
    override fun changePwd(id: String, pwd: String): Mono<Void> {
        val query = MonoQuery(Criteria.where("id").`is`(id))
        val update = Update().set("pwd", pwd)
        return reactiveMongoTemplate.updateFirst(query, update, User::class.java).then(Mono.empty())
    }

    override fun findPwdById(id: String): Mono<String> {
        val query = MonoQuery(Criteria.where("_id").`is`(id))
        query.fields().include("pwd").exclude("_id")
        val colName = reactiveMongoTemplate.getCollectionName(User::class.java)
        return reactiveMongoTemplate.findOne(query, Document::class.java, colName)
                .filter{ it.size > 0}
                .map { it["pwd"] as String }
                .switchIfEmpty(Mono.empty())
    }
}

interface OrgRepository : ReactiveCrudRepository<Org, String> {
    fun findByIdStartingWithOrderByIdAsc(regex: String): Flux<Org>
}

interface UserRepository : IUserRepository, ReactiveCrudRepository<User, String> {
    fun findByOrgIdOrderByIdAsc(orgId: String): Flux<User>

//    @Query("{'id': ?0}", fields = "{'pwd': 1, '_id': 0}")
//    fun findPwdById(id: String): Mono<String>

    fun deleteByOrgId(orgId: String): Mono<Void>
}

interface SvcRepository : ReactiveCrudRepository<Svc, String> {
    fun findAllByOrderByIdAsc(): Flux<Svc>
}

interface RoleRepository : ReactiveCrudRepository<Role, String> {
    fun findAllByOrderByIdAsc(): Flux<Role>
}

interface DefaultRoleSvcResRepository : ReactiveCrudRepository<DefaultRoleSvcRes, String>

interface IAdminRepository : IUserRepository

class AdminRepositoryImpl @Autowired constructor(
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : IAdminRepository {
    override fun changePwd(id: String, pwd: String): Mono<Void> {
        val query = MonoQuery(Criteria.where("id").`is`(id))
        val update = Update().set("pwd", pwd)
        return reactiveMongoTemplate.updateFirst(query, update, Admin::class.java).then(Mono.empty())
    }

    override fun findPwdById(id: String): Mono<String> {
        val query = MonoQuery(Criteria.where("_id").`is`(id))
        query.fields().include("pwd").exclude("_id")
        val colName = reactiveMongoTemplate.getCollectionName(Admin::class.java)
        // 这种原始，还没日志
//        val col = reactiveMongoTemplate.getCollection(colName)
//        return Mono.from(col.find(query.queryObject).projection(query.fieldsObject))
        // 可以有日志, ReactiveMongoTemplate有日志
        return reactiveMongoTemplate.findOne(query, Document::class.java, colName)
                .map { it["pwd"] as String }
    }
}

interface AdminRepository : IAdminRepository, ReactiveCrudRepository<Admin, String>
//{
//    @Query("{'id': ?0}", fields = "{'pwd': 1, '_id': 0}")
//    fun findPwdById(id: String): Mono<String>
//}


