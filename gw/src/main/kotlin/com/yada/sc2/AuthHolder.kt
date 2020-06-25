package com.yada.sc2

import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*

object AuthHolder {
    const val authKey = "AuthKey"
    const val tokenKey = "TokenKey"

    private operator fun <T, R, V> ((T) -> R).rangeTo(other: (R) -> V): ((T) -> V) {
        return {
            other(this(it))
        }
    }

    fun login(username: String, password: String): Mono<String> =
            Mono.subscriberContext().flatMap { ctx ->
                val oAuth = ctx.getOrEmpty<Auth>(authKey)
                oAuth.map { auth ->
                    auth.login(username, password).flatMap {
                        Mono.just(it).subscriberContext { ctx ->
                            ctx.put(tokenKey, it)
                        }
                    }
                }.orElse(Mono.empty())
            }

    fun getUserInfo(): Mono<UserInfo> =
            Mono.subscriberContext().flatMap { ctx ->
                ctx.getOrEmpty<String>(tokenKey).flatMap { token ->
                    ctx.getOrEmpty<Auth>(authKey).map { auth ->
                        auth.getUserInfo(token)
                    }
                }.orElse(Mono.empty())
            }

    fun logout(): Mono<Void> =
            Mono.subscriberContext().flatMap { ctx ->
                ctx.getOrEmpty<String>(tokenKey).flatMap { token ->
                    ctx.getOrEmpty<Auth>(authKey).map { auth ->
                        auth.logout(token)
                    }
                }.orElse(Mono.empty())
            }

    fun <T> initContext(filterMono: Mono<T>, auth: Auth, token: String?): Mono<T> =
            checkAndRefreshUi(auth, token).flatMap { filterMono.subscriberContext(putToken(it)..putAuth(auth)) }

    fun getToken(): Mono<String> = Mono.subscriberContext().map { it.get<String>(tokenKey) }

    private fun newToken() = UUID.randomUUID().toString()

    private fun checkAndRefreshUi(auth: Auth, token: String?): Mono<String> = if (token != null) {
        val t: String = token
        auth.getUserInfo(t).map { auth.refreshToken(t) }.map { t }.defaultIfEmpty(newToken())
    } else {
        Mono.just(newToken())
    }

    private fun putToken(token: String) = { ctx: Context ->
        ctx.put(tokenKey, token)
    }

    private fun putAuth(auth: Auth) = { ctx: Context ->
        ctx.put(authKey, auth)
    }
}