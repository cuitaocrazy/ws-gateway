package com.yada.web.services

import com.yada.security.AuthInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 认帐服务接口
 */
interface IAuthenticationService {
    fun login(username: String, password: String): Mono<AuthInfo>
    fun logout(token: String): Mono<Void>
    fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean>
}

@Service
class AuthenticationService @Autowired constructor(
        private val userService: IUserService,
        private val pwdDigestService: IPwdDigestService,
        private val author: IAuthorizationService) : IAuthenticationService {
    override fun login(username: String, password: String): Mono<AuthInfo> =
            userService.getPwd(username).map { it == pwdDigestService.getPwdDigest(username, password) }.filter { it }
                    .flatMap { userService.get(username) }
                    .flatMap { user ->
                        author.getUserResList(user).map { resList -> AuthInfo.create(user, resList) }
                    }

    override fun logout(token: String): Mono<Void> = Mono.empty()

    override fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean> =
            userService.getPwd(username)
                    .map { it == pwdDigestService.getPwdDigest(username, oldPassword) }
                    .filter { it }
                    .flatMap {
                        userService.changePwd(username, pwdDigestService.getPwdDigest(username, newPassword)).then(Mono.just(true))
                    }
                    .defaultIfEmpty(false)
}