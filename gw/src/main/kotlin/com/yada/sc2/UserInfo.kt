package com.yada.sc2

data class UserInfo(
        val orgId: String,
        val userId: String,
        val powers: List<Power>,
        val scope: String
)