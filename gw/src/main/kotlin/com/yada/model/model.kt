package com.yada.model

enum class Operator(val op: String) {
    READ("READ"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE")
}

data class Org(
        val id: String,
        val name: String
)

data class User(
        val id: String,
        val orgId: String,
        val roles: Set<String>
)

data class Res(val uri: String, val ops: Set<Operator>)

data class Svc(val id: String, val resources: Set<Res>)

data class Role(
        val id: String,
        val svcs: Set<Svc>
)

