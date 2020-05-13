package com.yada.security

import io.jsonwebtoken.Claims
import org.springframework.web.reactive.function.server.ServerRequest

class AdminAuthInfoParser(private val jwtTokenUtil: JwtTokenUtil) {
    fun getInfo(req: ServerRequest) =
            jwtTokenUtil.getBody(req.cookies()[jwtTokenCookiesName]?.run { this[0]?.value })?.run {
                val isAdmin = this[isAdminKey] as Boolean?
                if (isAdmin != null && isAdmin) {
                    AdminInfo(this[Claims.SUBJECT] as String)
                } else {
                    null
                }
            }
}