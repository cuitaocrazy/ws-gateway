package com.yada.security

import com.hazelcast.core.ExecutionCallback
import com.hazelcast.core.IMap
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

typealias TokenMap<T> = IMap<String, T>

fun <T> TokenMap<T>.getValue(token: String): Mono<T> =
        Mono.create {
            this.getAsync(token).andThen(object : ExecutionCallback<T> {
                override fun onFailure(t: Throwable) {
                    it.error(t)
                }

                override fun onResponse(response: T) {
                    it.success(response)
                }

            })
        }

fun <T> TokenMap<T>.putValue(token: String, value: T, ttl: Long): Mono<T> =
        Mono.create {
            this.putAsync(token, value, ttl, TimeUnit.SECONDS).andThen(object : ExecutionCallback<T> {
                override fun onFailure(t: Throwable) {
                    it.error(t)
                }

                override fun onResponse(response: T) {
                    it.success(response)
                }
            })
        }

fun <T> TokenMap<T>.deleteToken(token: String): Mono<T> =
        Mono.create {
            this.removeAsync(token).andThen(object : ExecutionCallback<T> {
                override fun onFailure(t: Throwable) {
                    it.error(t)
                }

                override fun onResponse(response: T) {
                    it.success(response)
                }

            })
        }

fun <T> TokenMap<T>.hasToken(token: String): Mono<Boolean> =
        this.getValue(token).map { true }.defaultIfEmpty(false)

class TokenManager @Autowired constructor(private val map: IMap<String, AuthInfo>) {
    fun hasToken(token: String): Mono<Boolean> = map.hasToken(token)
    fun put(token: String, value: AuthInfo, ttl: Long) = map.putValue(token, value, ttl)
    fun get(token: String) = map.getValue(token)
    fun delete(token: String) = map.deleteToken(token)
}
