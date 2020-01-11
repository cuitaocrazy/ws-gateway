package com.yada.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

enum class Operator(val op: String) {
    READ("READ"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE")
}

data class RoleId(val appId: String, val roleName: String)

@Document
data class Org(
        @Id
        val id: String,
        val name: String
)

data class User(
        val id: String,
        val pwd: String,
        val orgId: String,
        val roles: Set<RoleId>
)

data class Res(val uri: String, val ops: Set<Operator>)


data class Svc(val svcId: String, val resources: Set<Res>)

typealias SvcRes = Svc

data class Role(
        val id: RoleId,
        val resources: Set<SvcRes>
)

data class App(
        val id: String,
        val resources: Set<SvcRes>,
        val roles: Set<Role>
)


