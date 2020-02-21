package com.yada

import com.yada.model.Res
import com.yada.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.impl.DefaultClaims
import java.security.Principal

class AuthInfo(private val claims: Claims) : Claims by claims, Principal {
    companion object {
        fun create(user: User, resList: List<Res>) = AuthInfo().apply {
            isAdmin = false
            this.user = user
            this.resList = resList
            this.username = user.id
        }

        fun create() = AuthInfo().apply { isAdmin = true }
    }

    constructor() : this(DefaultClaims())

    var isAdmin: Boolean?
        get() = this["isAdmin"] as Boolean?
        set(value) {
            this["isAdmin"] = value
        }

    var user: User?
        get() = this["userInfo", User::class.java]
        set(value) {
            this["userInfo"] = value
        }

    var resList: List<Res>?
        @Suppress("UNCHECKED_CAST")
        get() = this["resList", List::class.java] as List<Res>
        set(value) {
            this["resList"] = value
        }

    var username: String?
        get() = this.subject
        set(value) {
            this.subject = value
        }

    override fun getName(): String? = this.username
}