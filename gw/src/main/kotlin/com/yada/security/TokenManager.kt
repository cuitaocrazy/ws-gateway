package com.yada.security

import reactor.core.publisher.Mono


interface TokenManager {
    fun put(token: String, value: UserInfo, ttl: Long): Mono<UserInfo>
    fun put(token: String, value: UserInfo): Mono<UserInfo>
    fun get(token: String): Mono<UserInfo>
    fun delete(token: String): Mono<UserInfo>
}