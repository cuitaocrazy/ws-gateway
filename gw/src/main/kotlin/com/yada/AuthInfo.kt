package com.yada

import com.yada.model.Operator
import com.yada.model.Res
import com.yada.model.RoleId
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

    var isAdmin: Boolean
        get() = this["isAdmin"] as Boolean
        set(value) {
            this["isAdmin"] = value
        }

    @Suppress("UNCHECKED_CAST")
    var user: User?
        get() = this["userInfo"]?.run {
            when (this) {
                is Map<*, *> -> convertUser(this as Map<String, Any?>)
                else -> throw Error("未知")
            }
        }
        set(value) {
            this["userInfo"] = value
        }

    var resList: List<Res>?
        @Suppress("UNCHECKED_CAST")
        get() = (this["resList"]).run {
            (this as List<*>).map { convertRes(it as Map<String, Any?>) }
        }
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

@Suppress("UNCHECKED_CAST")
private fun convertUser(map: Map<String, Any?>): User = User(map["id"] as String, map["orgId"] as String, (map["roles"] as List<Map<String, Any?>>).map(::convertRoleId).toSet())

@Suppress("UNCHECKED_CAST")
private fun convertRes(map: Map<String, Any?>): Res = Res(map["uri"] as String, (map["ops"] as List<String>).map(::convertOp).toSet())

private fun convertOp(opStr: String): Operator = Operator.valueOf(opStr)

private fun convertRoleId(map: Map<String, Any?>): RoleId = RoleId(map["appId"] as String, map["roleName"] as String)