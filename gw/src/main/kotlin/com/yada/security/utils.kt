package com.yada.security

import com.yada.TimeUtil
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

const val authInfoKey = "authInfo"
const val tokenKey = "token"
const val isAdminKey = "isAdmin"
const val adminInfoKey = "adminInfo"

val ServerRequest.authInfo: Optional<AuthInfo>
    get() = Optional.ofNullable(this.attributes()[authInfoKey] as AuthInfo?)

val ServerRequest.token: Optional<String>
    get() = Optional.ofNullable(this.attributes()[tokenKey] as String?)

val ServerRequest.isAdmin: Boolean
    get() = this.attributes()[isAdminKey] as Boolean? ?: false

val ServerRequest.adminInfo: AdminInfo
    get() = this.attributes()[adminInfoKey] as AdminInfo

const val jwtTokenCookiesName = "AUTH_ID"

class TokenUtil {
    fun generateToken() = UUID.randomUUID().toString()
}

class ResponseWithCookies(tokenManager: TokenManager, jwtTokenUtil: JwtTokenUtil, ttl: Long, timeUtil: TimeUtil, tokenUtil: TokenUtil) {
    companion object {
        private var jwtTokenUtil: JwtTokenUtil? = null
        private var ttl: Long? = null
        private var tokenManager: TokenManager? = null
        private var timeUtil: TimeUtil? = null
        private var tokenUtil: TokenUtil? = null

        fun createServerResponse(token: String,
                                 path: String,
                                 bodyBuilder: ServerResponse.BodyBuilder
        ): Mono<ServerResponse> {
            val currentDate = timeUtil!!.getCurrentDate()
            return tokenManager!!.get(token).flatMap {
                val jwt = jwtTokenUtil!!.generatorJwt(token, it.user.id, currentDate, ttl!!)
                bodyBuilder
                        .cookie(
                                ResponseCookie.from(jwtTokenCookiesName, jwt)
                                        .maxAge(Duration.ofMillis(ttl!! * 1000))
                                        .path(path)
                                        .build()
                        ).build()
            }
        }

        fun createAdminServerResponse(adminInfo: AdminInfo,
                                      path: String,
                                      bodyBuilder: ServerResponse.BodyBuilder
        ): Mono<ServerResponse> {
            val currentDate = timeUtil!!.getCurrentDate()
            val jwt = jwtTokenUtil!!.generatorAdminJwt(adminInfo.id, currentDate, ttl!!)
            return bodyBuilder
                    .cookie(
                            ResponseCookie.from(jwtTokenCookiesName, jwt)
                                    .maxAge(Duration.ofMillis(ttl!! * 1000))
                                    .path(path)
                                    .build()
                    ).build()
        }

        fun createLogoutServerResponse(
                req: ServerRequest,
                path: String,
                bodyBuilder: ServerResponse.BodyBuilder
        ): Mono<ServerResponse> =
                req.token.map { tokenManager!!.delete(it) }
                        .orElse(Mono.empty())
                        .then(
                                bodyBuilder
                                        .cookie(ResponseCookie.from(jwtTokenCookiesName, "")
                                                .path(path)
                                                .build()
                                        )
                                        .build()
                        )

        fun createAdminLogoutServerResponse(path: String, bodyBuilder: ServerResponse.BodyBuilder): Mono<ServerResponse> =
                bodyBuilder.cookie(ResponseCookie.from(jwtTokenCookiesName, "")
                        .path(path)
                        .build()
                ).build()
    }

    init {
        ResponseWithCookies.jwtTokenUtil = jwtTokenUtil
        ResponseWithCookies.ttl = ttl
        ResponseWithCookies.tokenManager = tokenManager
        ResponseWithCookies.timeUtil = timeUtil
        ResponseWithCookies.tokenUtil = tokenUtil
    }
}