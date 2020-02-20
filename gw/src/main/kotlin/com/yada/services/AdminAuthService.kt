package com.yada.services

import com.yada.AuthInfo
import com.yada.JwtTokenUtil
import com.yada.model.Operator
import com.yada.repository.IAdminUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface IAdminAuthService {
    fun login(username: String, password: String): Mono<AuthInfo>
    fun logout(token: String): Mono<Void>
    fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean>
    fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean>
}

@Service
class AdminAuthService @Autowired constructor(private val adminUserRepo: IAdminUserRepository, private val pwdDigestService: IPwdDigestService, private val jwtUtil: JwtTokenUtil) : IAdminAuthService {
    override fun login(username: String, password: String): Mono<AuthInfo> = Mono.just(username == "admin")
            .filter { it }
            .map { pwdDigestService.getPwdDigest("admin", password) }
            .flatMap { pwdDigest ->
                adminUserRepo.getAdminUser()
                        .map { pwdDigest == it.pwd }
                        .defaultIfEmpty(pwdDigestService.getDefaultPwdDigest("admin") == pwdDigest)
                        .filter { it }
                        .map { AuthInfo.create() }
            }

    override fun logout(token: String): Mono<Void> = Mono.empty()

    override fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean> = Mono.just(jwtUtil.getEntity(token) != null)

    override fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean> = if (username == "admin") {
        val newPwdDigest = pwdDigestService.getPwdDigest("admin", newPassword)
        val oldPwdDigest = pwdDigestService.getPwdDigest("admin", oldPassword)

        adminUserRepo.getAdminUser()
                .map { it.pwd == oldPwdDigest }
                .defaultIfEmpty(pwdDigestService.getDefaultPwdDigest("admin") == oldPwdDigest)
                .filter { it }
                .flatMap { adminUserRepo.changePwd(newPwdDigest).then(Mono.just(true)) }
                .defaultIfEmpty(false)
    } else {
        Mono.just(false)
    }
}