package com.yada.config

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.yada.gateways.ApiAuthGatewayFilterFactory
import com.yada.gateways.AuthGatewayFilterFactory
import com.yada.security.*
import com.yada.security.hazelcast.HazelcastTokenManager
import com.yada.web.security.GeneralAuth
import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

typealias TokenManagerCreator = (String) -> TokenManager

@Configuration
@EnableConfigurationProperties(SecurityConfigProperties::class)
open class SecurityConfig(
        private val config: SecurityConfigProperties,
        private val stringEncryptor: StringEncryptor,
        @Value("\${yada.contextPath:}")
        private val contextPath: String
) {

    @Bean
    open fun pwdDigestService(): IPwdDigestService = PwdDigestService(config.defaultPwd, stringEncryptor)

    @Bean
    open fun recaptchaService(): IRecaptchaService = when (config.recaptcha.type) {
        SecurityConfigProperties.RecaptchaType.Google -> GoogleRecaptchaService(config.recaptcha)
        SecurityConfigProperties.RecaptchaType.GoogleCN -> GoogleCnRecaptchaService(config.recaptcha)
        else -> NoneRecaptchaService()
    }

    @Bean
    open fun pwdStrengthService(): IPwdStrengthService = PwdStrengthService(config.pwdStrength)

    @Bean
    open fun tokenManagerCreator(client: HazelcastInstance): TokenManagerCreator = { name: String ->
        HazelcastTokenManager(client.getMap(name), config.token.expire)
    }

    @Bean
    open fun tokenManager(map: IMap<String, String>): HazelcastTokenManager = HazelcastTokenManager(map, config.token.expire)

    @Bean
    open fun apiAuthGatewayFilterFactory(generalAuth: GeneralAuth) = ApiAuthGatewayFilterFactory(generalAuth)

    @Bean
    open fun authGatewayFilterFactory(generalAuth: GeneralAuth) = AuthGatewayFilterFactory(generalAuth, contextPath)
}
