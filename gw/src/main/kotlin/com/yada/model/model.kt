package com.yada.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table
data class Org(
        @Id val id: String,
        val name: String,
        @MappedCollection
        val users: Set<User>
)

@Table
data class User(
        @Id
        val id: String,
        val pwd: String,
        @Column("org_id")
        val org: Org
)

@Table
data class App(
        @Id
        val id: String,
        val resources: Set<Res>
)

@Table
data class Svc(
        @Id
        val id: String,
        val resources: Set<Res>
)

data class Role(
        val id: String,
        val name: String
)

enum class Operator(val op: String) {
    READ("READ"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE")
}

data class Res(val uri: String, val ops: Set<Operator>)

data class ResWithSvc(val svcId: String, val res: Res) {
    fun getUri() = "/" + this.svcId + this.res.uri
}
