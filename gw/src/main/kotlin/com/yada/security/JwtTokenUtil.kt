package com.yada.security

import com.yada.TimeUtil
import io.jsonwebtoken.*
import org.springframework.http.ResponseCookie
import java.time.Duration
import java.util.*

/**
 * 参考：https://dzone.com/articles/spring-boot-security-json-web-tokenjwt-hello-world
 */
class JwtTokenUtil(
        private val secret: String,
        private val tokenExpireInterval: Long,
        private val timeUtil: TimeUtil
) {
    private fun Date.getExpirationDate() = Date(this.time + tokenExpireInterval * 1000)

    private fun JwtBuilder.generateToken() = this.signWith(SignatureAlgorithm.HS512, secret).compact()

    private val emptyCookie = ResponseCookie.from("token", "")
            .path(getPath(false)).maxAge(0).build()

    private val adminEmptyCookie = ResponseCookie.from("token", "")
            .path(getPath(true)).maxAge(0).build()

    fun getEmptyCookie(entity: AuthInfo): ResponseCookie = if (entity.isAdmin) adminEmptyCookie else emptyCookie

    fun getEntity(token: String) =
            try {
                AuthInfo(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body)
            } catch (_: SignatureException) {
                null
            } catch (_: ExpiredJwtException) {
                null
            }

    fun generateToken(entity: AuthInfo, currentDate: Date = timeUtil.getCurrentDate()): String =
            Jwts.builder()
                    .setClaims(entity)
                    .setIssuedAt(currentDate)
                    .setExpiration(currentDate.getExpirationDate()).generateToken()

    fun generateCookie(entity: AuthInfo, currentDate: Date = timeUtil.getCurrentDate()): ResponseCookie {
        val token = generateToken(entity, currentDate)
        return generateCookie(
                token,
                Duration.ofMillis(entity.expiration.time - currentDate.time),
                getPath(entity.isAdmin)
        )
    }

    fun renewCookie(entity: AuthInfo, currentDate: Date = timeUtil.getCurrentDate()): ResponseCookie =
            generateCookie(
                    Jwts.builder()
                            .setClaims(entity)
                            .setIssuedAt(currentDate)
                            .setExpiration(currentDate.getExpirationDate()).generateToken(),
                    Duration.ofMillis(tokenExpireInterval * 1000),
                    getPath(entity.isAdmin))

    private fun generateCookie(token: String, maxAge: Duration, path: String) =
            ResponseCookie.from("token", token)
                    .maxAge(maxAge)
                    .path(path)
                    .build()

    private fun getPath(isAdmin: Boolean?) = if (isAdmin == true) "/admin" else "/"
}