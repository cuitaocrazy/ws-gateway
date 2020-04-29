package com.yada.web.services

import com.yada.security.AuthInfo
import com.yada.security.JwtTokenUtil
import com.yada.web.model.Operator
import com.yada.web.repository.IAdminUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface IAdminAuthService {
    fun login(username: String, password: String): Mono<AuthInfo>
    fun logout(token: String): Mono<Void>
    fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean>
    fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean>
}

private const val adminStr = "admin"

@Service
class AdminAuthService @Autowired constructor(
        private val adminUserRepo: IAdminUserRepository,
        private val pwdDigestService: IPwdDigestService,
        private val jwtUtil: JwtTokenUtil
) : IAdminAuthService {
    override fun login(username: String, password: String): Mono<AuthInfo> =
            if (username == adminStr) {
                adminUserRepo
                        .getAdminUser()
                        .map { it.pwd }
                        .defaultIfEmpty(pwdDigestService.getDefaultPwdDigest(adminStr))
                        .filter { pwdDigestService.getPwdDigest(adminStr, password) == it }
                        .map { AuthInfo.create() }
            } else {
                Mono.empty()
            }

    override fun logout(token: String): Mono<Void> = Mono.empty()

    override fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean> =
            Mono.just(jwtUtil.getEntity(token) != null)

    override fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean> =
            if (username == adminStr) {
                val newPwdDigest = pwdDigestService.getPwdDigest(adminStr, newPassword)
                val oldPwdDigest = pwdDigestService.getPwdDigest(adminStr, oldPassword)

                adminUserRepo.getAdminUser()
                        .map { it.pwd == oldPwdDigest }
                        .defaultIfEmpty(pwdDigestService.getDefaultPwdDigest(adminStr) == oldPwdDigest)
                        .filter { it }
                        .flatMap { adminUserRepo.changePwd(newPwdDigest).then(Mono.just(true)) }
                        .defaultIfEmpty(false)
            } else {
                Mono.just(false)
            }
}