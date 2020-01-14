package com.yada.controllers

import com.yada.model.RoleId
import com.yada.model.User
import com.yada.repository.UserRepository
import com.yada.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(private val userService: IUserService, private val repo: UserRepository) {
    @GetMapping
    fun get() = userService.getByOrgId("00")
    @GetMapping("save")
    fun save(): Flux<User> = repo.saveAll(listOf(
            User("cuitao", "00", setOf(
                    RoleId("app1", "role1")
            )),
            User("ct", "00", setOf(
                    RoleId("app1", "role1"),
                    RoleId("app2", "role1")
            ))
    ))
    @GetMapping("changePwd")
    fun changePwd() = userService.changePwd("cuitao", "cuitaopwd123")
}