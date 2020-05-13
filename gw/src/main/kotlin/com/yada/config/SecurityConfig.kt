package com.yada.config

import com.hazelcast.core.IMap
import com.yada.security.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SecurityConfigProperties::class)
open class SecurityConfig(private val config: SecurityConfigProperties) {
    @Bean
    open fun jwtTokenUtil() =
            JwtTokenUtil(config.token.secret)

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
    open fun tokenManager(map: IMap<String, AuthInfo>): TokenManager = TokenManager(map)

    @Bean
    open fun authInfoParser(tokenManager: TokenManager, jwtTokenUtil: JwtTokenUtil) =
            AuthInfoParser(tokenManager, jwtTokenUtil)

    @Bean
    open fun adminAuthInfoParser(jwtTokenUtil: JwtTokenUtil) = AdminAuthInfoParser(jwtTokenUtil)

    @Bean
    open fun webFluxAuthFilter(authInfoParser: AuthInfoParser) = WebFluxAuthFilter(authInfoParser)

    @Bean
    open fun webFluxAdminAuthFilter(adminAuthInfoParser: AdminAuthInfoParser) =
            WebFluxAdminAuthFilter(adminAuthInfoParser)
}
