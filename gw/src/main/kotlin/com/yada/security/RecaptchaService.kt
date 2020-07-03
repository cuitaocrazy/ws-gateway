package com.yada.security

import com.yada.config.SecurityConfigProperties
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

interface IRecaptchaService {
    fun check(code: String): Mono<Boolean>
    fun getCode(data: Map<String, String>): String?
}

abstract class AbsGoogleRecaptchaService(private val url: String) : IRecaptchaService {
    override fun check(code: String): Mono<Boolean> {
        return WebClient
                .create(url + code)
                .post()
                .retrieve()
                .bodyToMono<MutableMap<String, Any>>()
                .map { it["success"] as Boolean }
    }

    override fun getCode(data: Map<String, String>): String? = data["g-recaptcha-response"]
}

/**
 * 墙外验证码
 */
open class GoogleRecaptchaService(config: SecurityConfigProperties.RecaptchaProperties) : AbsGoogleRecaptchaService("https://www.google.com/recaptcha/api/siteverify?secret=${config.secret}")

/**
 * 墙内验证码
 */
open class GoogleCnRecaptchaService(config: SecurityConfigProperties.RecaptchaProperties) : AbsGoogleRecaptchaService("https://recaptcha.net/recaptcha/api/siteverify?secret=${config.secret}")

open class NoneRecaptchaService : IRecaptchaService {
    override fun check(code: String): Mono<Boolean> = Mono.just(true)
    override fun getCode(data: Map<String, String>): String? = "none"
}