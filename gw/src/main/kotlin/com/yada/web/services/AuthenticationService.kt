package com.yada.web.services

import com.yada.security.AuthInfo
import com.yada.security.IPwdDigestService
import com.yada.security.TokenManager
import com.yada.security.TokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

/**
 * 认帐服务接口
 */
interface IAuthenticationService {
    fun login(username: String, password: String): Mono<String>
    fun logout(token: String): Mono<Void>
    fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean>
    fun refreshToken(token: String): Mono<Void>
}

@Service
open class AuthenticationService @Autowired constructor(
        private val userService: IUserService,
        private val pwdDigestService: IPwdDigestService,
        private val author: IAuthorizationService,
        private val tokenManager: TokenManager,
        private val tokenUtil: TokenUtil,
        private val ttl: Long
) : IAuthenticationService {
    override fun login(username: String, password: String): Mono<String> =
            userService.getPwd(username)
                    .map { it == pwdDigestService.getPwdDigest(username, password) }
                    .filter { it }
                    .flatMap { userService.get(username) }
                    .flatMap { user ->
                        author.getUserResList(user).map { resList -> AuthInfo(user, resList) }
                    }.flatMap {
                        val token = tokenUtil.generateToken()
                        tokenManager.put(token, it, ttl).then(Mono.just(token))
                    }

    override fun logout(token: String): Mono<Void> = tokenManager.delete(token).then()

    override fun refreshToken(token: String): Mono<Void> =
            tokenManager.get(token).flatMap { tokenManager.put(token, it, ttl) }.then()

    @Transactional
    override fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean> =
            userService.getPwd(username)
                    .map { it == pwdDigestService.getPwdDigest(username, oldPassword) }
                    .filter { it }
                    .flatMap {
                        userService.changePwd(username, pwdDigestService.getPwdDigest(username, newPassword))
                                .then(Mono.just(true))
                    }
                    .defaultIfEmpty(false)
}