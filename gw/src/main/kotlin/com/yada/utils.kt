package com.yada

import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono


fun <T> withNotFound(m: Mono<T>): Mono<ResponseEntity<T>> = m.map { ResponseEntity.ok().body(it) }.defaultIfEmpty(ResponseEntity.notFound().build())