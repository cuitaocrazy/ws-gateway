package com.yada.services

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

interface IRecaptchaService {
    fun check(code: String): Mono<Boolean>
}

@Service
open class GoogleRecaptchaService : IRecaptchaService {
    private val url = "https://www.google.com/recaptcha/api/siteverify?secret=6Leu2NsUAAAAAKbXdSjLz6_c1cf8qX2bL4xfn1mN&response="
    override fun check(code: String): Mono<Boolean> {
        return WebClient
                .create(url + code)
                .post()
                .retrieve()
                .bodyToMono<MutableMap<String, Any>>()
                .map { it["success"] as Boolean }
    }
}