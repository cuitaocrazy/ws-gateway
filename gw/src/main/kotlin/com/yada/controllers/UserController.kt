package com.yada.controllers

import com.yada.model.User
import com.yada.services.IUserService
import com.yada.withNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(private val userService: IUserService) {
    @GetMapping
    fun getUsersBy(@RequestParam("org_id") orgId: String?) = userService.getByOrgId(orgId ?: "")

    @PutMapping
    fun createOrUpdate(@RequestBody user: User) = userService.createOrUpdate(user)

    @GetMapping("{id}")
    fun getUser(@PathVariable("id") id: String): Mono<ResponseEntity<User>> = withNotFound(userService.get(id))

    @DeleteMapping("{id}")
    fun deleteUser(@PathVariable("id") id: String) = userService.delete(id)

    @GetMapping("{id}/exist")
    fun existUser(@PathVariable("id") id: String): Mono<ResponseEntity<Boolean>> = withNotFound(userService.exist(id))
}


//    @GetMapping("save")
//    fun save(): Flux<User> = repo.saveAll(listOf(
//            User("cuitao", "00", setOf(
//                    RoleId("app1", "role1")
//            )),
//            User("ct", "00", setOf(
//                    RoleId("app1", "role1"),
//                    RoleId("app2", "role1")
//            ))
//    ))