package com.yada.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.yada.model.User
import com.yada.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IUserService {
    fun get(id: String): Mono<User>
    fun getByOrgId(orgId: String): Flux<User>
    fun createOrUpdate(user: User): Mono<User>
    fun delete(id: String): Mono<Void>
    fun deleteByOrgId(orgId: String): Mono<Void>
    fun exist(id: String): Mono<Boolean>
    fun getPwd(id: String): Mono<String>
    fun changePwd(id: String, pwd: String): Mono<Void>
    fun getAll(): Flux<User>
}

@Service
open class UserService @Autowired constructor(private val userRepo: UserRepository, private val pwdDigestService: IPwdDigestService) : IUserService {
    override fun get(id: String): Mono<User> = userRepo.findById(id)

    override fun getByOrgId(orgId: String): Flux<User> = userRepo.findByOrgIdOrderByIdAsc(orgId)

    @Transactional
    override fun createOrUpdate(user: User): Mono<User> = userRepo.findById(user.id).flatMap { userRepo.save(user) }
            .switchIfEmpty(userRepo.save(user).flatMap { userRepo.changePwd(it.id, pwdDigestService.getDefaultPwdDigest(it.id)).then(Mono.just(it)) })

    @Transactional
    override fun delete(id: String): Mono<Void> = userRepo.deleteById(id)

    @Transactional
    override fun deleteByOrgId(orgId: String): Mono<Void> = userRepo.deleteByOrgId(orgId)

    override fun exist(id: String): Mono<Boolean> = userRepo.existsById(id)

    override fun getPwd(id: String): Mono<String> = userRepo.fundOnPwd(id).map { ObjectMapper().readTree(it)["pwd"]?.asText() }

    @Transactional
    override fun changePwd(id: String, pwd: String): Mono<Void> = userRepo.changePwd(id, pwd)

    override fun getAll(): Flux<User> = userRepo.findAll()
}
