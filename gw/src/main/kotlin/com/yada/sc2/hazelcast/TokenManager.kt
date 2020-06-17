package com.yada.sc2.hazelcast

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hazelcast.core.ExecutionCallback
import com.hazelcast.core.IMap
import com.yada.sc2.TokenManager
import com.yada.sc2.UserInfo
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.TimeUnit

typealias TokenMap<T> = IMap<String, T>

fun TokenMap<String>.getValue(token: String): Mono<UserInfo> =
        Mono.create { sink ->
            this.getAsync(token).andThen(object : ExecutionCallback<String> {
                override fun onFailure(t: Throwable) {
                    sink.error(t)
                }

                override fun onResponse(response: String?) {
                    if(response == null) {
                        sink.success(null)
                    } else {
                        sink.success(jacksonObjectMapper().readValue<UserInfo>(response))
                    }
                }

            })
        }

fun TokenMap<String>.putValue(token: String, value: UserInfo, ttl: Long): Mono<UserInfo> =
        Mono.create {
            this.putAsync(token, jacksonObjectMapper().writeValueAsString(value), ttl, TimeUnit.SECONDS)
                    .andThen(object : ExecutionCallback<String> {
                        override fun onFailure(t: Throwable) {
                            it.error(t)
                        }

                        override fun onResponse(response: String?) {
                            it.success(value)
                        }
                    })
        }

fun TokenMap<String>.deleteToken(token: String): Mono<UserInfo> =
        Mono.create {
            this.removeAsync(token).andThen(object : ExecutionCallback<String> {
                override fun onFailure(t: Throwable) {
                    it.error(t)
                }

                override fun onResponse(response: String?) {
                    if(response == null) {
                        it.success(null)
                    } else {
                        it.success(jacksonObjectMapper().readValue<UserInfo>(response))
                    }
                }

            })
        }

class HazelcastTokenManager(private val map: IMap<String, String>, private val defaultTTL: Long) : TokenManager {
    override fun put(token: String, value: UserInfo, ttl: Long) = map.putValue(token, value, ttl)
    override fun put(token: String, value: UserInfo) = put(token, value, defaultTTL)
    override fun get(token: String) = map.getValue(token)
    override fun delete(token: String) = map.deleteToken(token)
    override fun generateToken(userInfo: UserInfo) = UUID.randomUUID().toString()
}