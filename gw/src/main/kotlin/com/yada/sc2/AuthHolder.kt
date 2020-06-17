package com.yada.sc2

import reactor.core.publisher.Mono

object AuthHolder {
    const val authKey = "AuthKey"
    const val tokenKey = "TokenKey"
    const val sendTokenFnKey = "sendTokenFnKey"

    fun login(username: String, password: String): Mono<String> =
            Mono.subscriberContext().flatMap { ctx ->
                val oAuth = ctx.getOrEmpty<Auth>(authKey)
                val oLoginCallback = ctx.getOrEmpty<(String) -> Unit>(sendTokenFnKey)
                oAuth.flatMap { auth ->
                    oLoginCallback.map { sendToken ->
                        object {
                            val auth = auth
                            val sendToken = sendToken
                        }
                    }
                }.map { authContext ->
                    authContext.auth.login(username, password).map {
                        authContext.sendToken(it)
                        it
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
}