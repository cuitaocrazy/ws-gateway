package com.yada.controllers

import com.yada.auths.IAuthorizationService
import com.yada.model.Operator
import com.yada.model.RoleId
import com.yada.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController @Autowired constructor(private val auth: IAuthorizationService) {
    @GetMapping
    fun get() = auth.authorize(User("cuitao", "00", setOf(RoleId("app1", "role1"))), "/svc1/api/test1", Operator.READ)
}
