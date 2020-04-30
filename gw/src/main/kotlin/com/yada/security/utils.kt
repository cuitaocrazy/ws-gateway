package com.yada.security

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebExchange


val ServerWebExchange.token: String?
    get() = this.request.cookies["token"]?.run { this[0]?.value }

val ServerRequest.token: String?
    get() = this.cookies()["token"]?.run { this[0]?.value }

var ServerRequest.authInfo: AuthInfo
    get() = this.attributes()["authInfo"]!! as AuthInfo
    set(value) {
        this.attributes()["authInfo"] = value
    }