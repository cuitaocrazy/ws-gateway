package com.yada.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "yada.security")
class SecurityConfigProperties(
        var defaultPwd: String = "changepwd",
        var recaptcha: String = "recaptcha.None",
        var pwdStrength: Int = 1,
        var token: TokenProperties = TokenProperties()
) {
    data class TokenProperties(var secret: String = "yadajwt", var expire: Long = 3600)
}