package com.yada.config

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.yada.gateways.ApiAuthGatewayFilterFactory
import com.yada.gateways.AuthGatewayFilterFactory
import com.yada.sc2.TokenManager
import com.yada.sc2.hazelcast.HazelcastTokenManager
import com.yada.security.*
import com.yada.web.security.GeneralAuth
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

typealias TokenManagerCreator =  (String) -> TokenManager

@Configuration
@EnableConfigurationProperties(SecurityConfigProperties::class)
open class SecurityConfig(private val config: SecurityConfigProperties) {

    @Bean
    open fun pwdDigestService(): IPwdDigestService = PwdDigestService(config.defaultPwd)

    @Bean
    open fun recaptchaService(): IRecaptchaService = when (config.recaptcha) {
        "recaptcha.Google" -> GoogleRecaptchaService()
        "recaptcha.GoogleCN" -> GoogleCnRecaptchaService()
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
    open fun authGatewayFilterFactory(generalAuth: GeneralAuth) = AuthGatewayFilterFactory(generalAuth)
}
