package com.yada.security

enum class Operator(val op: String) {
    READ("READ"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE")
}