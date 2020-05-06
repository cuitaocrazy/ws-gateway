package com.yada.security

import com.hazelcast.core.IMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

typealias TokenMap = IMap<String, String>

fun TokenMap.getValue(token: String): Mono<String> =
        Mono.fromCallable<String> { this[token] }.subscribeOn(Schedulers.elastic())
fun TokenMap.putValue(token: String, value: String): Mono<String> =
        Mono.fromCallable<String> { this.put(token, value) }.subscribeOn(Schedulers.elastic())
fun TokenMap.deleteToken(token: String): Mono<Void> =
        Mono.fromRunnable<Void> { this.delete(token) }.subscribeOn(Schedulers.elastic())
fun TokenMap.hasToken(token: String): Mono<Boolean> =
        Mono.fromCallable{ this.containsKey(token)}.subscribeOn(Schedulers.elastic())
@Component
class TokenManager(@Autowired private val map: IMap<String, String>) {
    fun hasToken(token: String): Mono<Boolean> = map.hasToken(token)
    fun put(token: String, value: String) = map.putValue(token, value)
    fun get(token: String) = map.getValue(token)
    fun delete(token: String) = map.deleteToken(token)
}