package com.yada.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.yada.model.User
import com.yada.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface IUserService {
    fun get(id: String): Mono<User>
    fun getByOrgId(orgId: String): Flux<User>
    fun createOrUpdate(user: User): Mono<User>
    fun delete(id: String): Mono<Void>
    fun exist(id: String): Mono<Boolean>
    fun getPwd(id: String): Mono<String>
    fun changePwd(id: String, pwd: String): Mono<Void>
}

@Service
class UserService @Autowired constructor(private val userRepo: UserRepository) : IUserService {
    override fun get(id: String): Mono<User> = userRepo.findById(id)

    override fun getByOrgId(orgId: String): Flux<User> = userRepo.findByOrgId(orgId)

    override fun createOrUpdate(user: User): Mono<User> = userRepo.save(user)

    override fun delete(id: String): Mono<Void> = userRepo.deleteById(id)

    override fun exist(id: String): Mono<Boolean> = userRepo.existsById(id)

    override fun getPwd(id: String): Mono<String> = userRepo.fundOnPwd(id).map { ObjectMapper().readTree(it)["pwd"]?.asText() }

    override fun changePwd(id: String, pwd: String): Mono<Void> = userRepo.changePwd(id, pwd)
}
