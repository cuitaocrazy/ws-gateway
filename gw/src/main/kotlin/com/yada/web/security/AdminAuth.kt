package com.yada.web.security

import com.yada.config.TokenManagerCreator
import com.yada.security.AuthWithTokenManager
import com.yada.security.IPwdDigestService
import com.yada.security.UserInfo
import com.yada.web.repository.AdminRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AdminAuth @Autowired constructor(
        private val adminRepo: AdminRepository,
        private val pwdDigestService: IPwdDigestService,
        creator: TokenManagerCreator
) : AuthWithTokenManager({ creator("admin") }) {

    override fun checkAndGet(username: String, password: String): Mono<UserInfo> =
            adminRepo.findPwdById(username).filter {
                pwdDigestService.checkPwdDigest(username, password, it)
            }.map {
                UserInfo(username, listOf(), mapOf())
            }

    override fun getPath(): String = "/admin"

}