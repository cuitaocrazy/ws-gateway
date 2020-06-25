package com.yada.sc2

import reactor.core.publisher.Mono

/**
 * 认证
 */
interface Auth {
    fun login(username: String, password: String): Mono<String>

    fun checkAndGet(username: String, password: String): Mono<UserInfo>

    fun logout(token: String): Mono<Void>

    fun refreshToken(token: String): Mono<Void>

    fun getUserInfo(token: String): Mono<UserInfo>

    fun getPath(): String
}