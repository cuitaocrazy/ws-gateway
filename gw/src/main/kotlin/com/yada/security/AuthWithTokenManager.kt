package com.yada.security

import reactor.core.publisher.Mono

/**
 * 有token管理的认证
 */
abstract class AuthWithTokenManager(creator: () -> TokenManager) : Auth {
    private var tokenManager = creator()
    override fun login(username: String, password: String): Mono<String> =
            Mono.subscriberContext().map { ctx ->
                ctx.get<String>(AuthHolder.tokenKey)
            }.flatMap { token ->
                checkAndGet(username, password).flatMap { ui ->
                    tokenManager.put(token, ui).map { token }
                }
            }
//            checkAndGet(username, password).flatMap { ui ->
//                val token = tokenManager.generateToken(ui)
//                tokenManager.put(token, ui).map { token }
//            }

    override fun logout(token: String): Mono<Void> = tokenManager.delete(token).then()

    override fun refreshToken(token: String): Mono<Void> =
            tokenManager.get(token).flatMap { tokenManager.put(token, it) }.then()

    override fun getUserInfo(token: String): Mono<UserInfo> = tokenManager.get(token)
}