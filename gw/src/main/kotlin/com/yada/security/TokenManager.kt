package com.yada.security

import com.hazelcast.core.ExecutionCallback
import com.hazelcast.core.IMap
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

typealias TokenMap = IMap<String, String>

fun TokenMap.getValue(token: String): Mono<String> =
        Mono.create {
            this.getAsync(token).andThen(object : ExecutionCallback<String> {
                override fun onFailure(t: Throwable) {
                    it.error(t)
                }

                override fun onResponse(response: String) {
                    it.success(response)
                }

            })
        }

fun TokenMap.putValue(token: String, value: String, ttl: Long): Mono<String> =
        Mono.create {
            this.putAsync(token, value, ttl, TimeUnit.SECONDS).andThen(object : ExecutionCallback<String> {
                override fun onFailure(t: Throwable) {
                    it.error(t)
                }

                override fun onResponse(response: String) {
                    it.success(response)
                }
            })
        }

fun TokenMap.deleteToken(token: String): Mono<String> =
        Mono.create {
            this.removeAsync(token).andThen(object : ExecutionCallback<String> {
                override fun onFailure(t: Throwable) {
                    it.error(t)
                }

                override fun onResponse(response: String) {
                    it.success(response)
                }

            })
        }


fun TokenMap.hasToken(token: String): Mono<Boolean> =
        this.getValue(token).map { true }.defaultIfEmpty(false)

class TokenManager @Autowired constructor(private val map: IMap<String, String>, private val ttl: Long) {
    fun hasToken(token: String): Mono<Boolean> = map.hasToken(token)
    fun put(token: String, value: String) = map.putValue(token, value, ttl)
    fun get(token: String) = map.getValue(token)
    fun delete(token: String) = map.deleteToken(token)
}