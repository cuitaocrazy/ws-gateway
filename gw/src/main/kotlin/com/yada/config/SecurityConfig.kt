package com.yada.config

import com.yada.TimeUtil
import com.yada.security.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SecurityConfigProperties::class)
open class SecurityConfig(private val config: SecurityConfigProperties) {
    @Bean
    open fun jwtTokenUtil() =
            JwtTokenUtil(config.token.secret, config.token.expire, TimeUtil())

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
}
