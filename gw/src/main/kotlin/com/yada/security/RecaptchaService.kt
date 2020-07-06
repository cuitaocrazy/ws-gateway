package com.yada.security

import com.yada.config.SecurityConfigProperties
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.ProxyProvider

interface IRecaptchaService {
    fun check(code: String): Mono<Boolean>
    fun getCode(data: Map<String, String>): String?
}

abstract class AbsGoogleRecaptchaService(
        private val url: String,
        private val proxyHost: String,
        private val proxyPort: Int
) : IRecaptchaService {

    override fun check(code: String): Mono<Boolean> {
        return buildWebClient(code)
                .post()
                .retrieve()
                .bodyToMono<MutableMap<String, Any>>()
                .map { it["success"] as Boolean }
    }

    override fun getCode(data: Map<String, String>): String? = data["g-recaptcha-response"]

    private fun buildWebClient(code: String): WebClient {
        if (StringUtils.hasText(proxyHost)) {
            val httpClient: HttpClient = HttpClient.create()
                    .tcpConfiguration { tcpClient ->
                        tcpClient.proxy {
                            it.type(ProxyProvider.Proxy.HTTP).host(proxyHost).port(proxyPort)
                        }
                    }
            val connector = ReactorClientHttpConnector(httpClient)
            return WebClient.builder().clientConnector(connector).baseUrl(url + code).build()
        } else {
            return WebClient.create(url + code)
        }
    }
}

/**
 * 墙外验证码
 */
open class GoogleRecaptchaService(config: SecurityConfigProperties.RecaptchaProperties) : AbsGoogleRecaptchaService(
        "https://www.google.com/recaptcha/api/siteverify?secret=${config.secret}", config.proxyHost, config.proxyPort
)

/**
 * 墙内验证码
 */
open class GoogleCnRecaptchaService(config: SecurityConfigProperties.RecaptchaProperties) : AbsGoogleRecaptchaService(
        "https://recaptcha.net/recaptcha/api/siteverify?secret=${config.secret}", config.proxyHost, config.proxyPort
)

open class NoneRecaptchaService : IRecaptchaService {
    override fun check(code: String): Mono<Boolean> = Mono.just(true)
    override fun getCode(data: Map<String, String>): String? = "none"
}