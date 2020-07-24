package com.yada.web.model

import com.yada.security.Operator
import org.springframework.data.mongodb.core.mapping.Document

//enum class Operator(val op: String) {
//    READ("READ"),
//    CREATE("CREATE"),
//    UPDATE("UPDATE"),
//    DELETE("DELETE")
//}

@Document
data class Org(
        val id: String,
        val name: String

)

@Document
data class User(
        val id: String,
        val orgId: String,
        val roles: Set<String>,
        val email: String
)

@Document
data class Res(val uri: String, val ops: Set<Operator>)

@Document
data class Svc(val id: String, val resources: Set<Res>)

@Document
data class Role(
        val id: String,
        val svcs: Set<Svc>
)

@Document
data class DefaultRoleSvcRes(val id: String, val resources: Set<Res>)

@Document
data class Admin(val id: String)
