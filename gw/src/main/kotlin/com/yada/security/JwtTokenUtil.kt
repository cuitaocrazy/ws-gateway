package com.yada.security

import io.jsonwebtoken.*
import java.util.*

/**
 * 参考：https://dzone.com/articles/spring-boot-security-json-web-tokenjwt-hello-world
 */


class JwtTokenUtil(
        private val secret: String
) {
    fun generatorJwt(token: String, userId: String, currentDate: Date, ttl: Long) =
            Jwts.builder()
                    .setClaims(mapOf("token" to token))
                    .setSubject(userId)
                    .setExpiration(Date(currentDate.time + ttl * 1000))
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact()

    fun generatorAdminJwt(userId: String, currentDate: Date, ttl: Long) =
            Jwts.builder()
                    .setSubject(userId)
                    .setClaims(mapOf("isAdmin" to true))
                    .setExpiration(Date(currentDate.time + ttl * 1000))
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact()

    fun getBody(jwt: String?): Claims? =
            try {
                if (jwt == null)
                    null
                else
                    Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).body
            } catch (_: SignatureException) {
                null
            } catch (_: ExpiredJwtException) {
                null
            }

    fun getToken(jwt: String?): String? =
            try {
                if (jwt == null)
                    null
                else
                    Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).body["token"] as String?
            } catch (_: SignatureException) {
                null
            } catch (_: ExpiredJwtException) {
                null
            }

//    private fun getPath(isAdmin: Boolean) = if (isAdmin) "/admin" else "/"
//
//    private val emptyCookie = ResponseCookie.from("token", "")
//            .path(getPath(false)).maxAge(0).build()
//
//    private val adminEmptyCookie = ResponseCookie.from("token", "")
//            .path(getPath(true)).maxAge(0).build()

//    fun getEmptyCookie(entity: AuthInfo): ResponseCookie = if (entity.isAdmin) adminEmptyCookie else emptyCookie
//
//    fun getEntity(token: String) =
//            try {
//                AuthInfo(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body)
//            } catch (_: SignatureException) {
//                null
//            } catch (_: ExpiredJwtException) {
//                null
//            }
//
//    fun generateToken(entity: AuthInfo, currentDate: Date = timeUtil.getCurrentDate()): String =
//            Jwts.builder()
//                    .setClaims(entity)
//                    .setIssuedAt(currentDate)
//                    .setExpiration(currentDate.getExpirationDate()).generateToken()
//
//    fun generateCookie(entity: AuthInfo, currentDate: Date = timeUtil.getCurrentDate()): ResponseCookie {
//        val token = generateToken(entity, currentDate)
//        return generateCookie(
//                token,
//                Duration.ofMillis(entity.expiration.time - currentDate.time),
//                getPath(entity.isAdmin)
//        )
//    }
//
//    fun renewCookie(entity: AuthInfo, currentDate: Date = timeUtil.getCurrentDate()): ResponseCookie =
//            generateCookie(
//                    Jwts.builder()
//                            .setClaims(entity)
//                            .setIssuedAt(currentDate)
//                            .setExpiration(currentDate.getExpirationDate()).generateToken(),
//                    Duration.ofMillis(tokenExpireInterval * 1000),
//                    getPath(entity.isAdmin))
//
//    private fun generateCookie(token: String, maxAge: Duration, path: String) =
//            ResponseCookie.from("token", token)
//                    .maxAge(maxAge)
//                    .path(path)
//                    .build()
//

}