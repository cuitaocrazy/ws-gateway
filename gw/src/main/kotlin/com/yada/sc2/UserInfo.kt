package com.yada.sc2

data class UserInfo(
        val userId: String,
        val powers: List<Power>,
        val profiles: Map<String, String>
)