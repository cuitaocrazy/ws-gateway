package com.yada.controllers

import com.yada.repository.OrgRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/org")
class OrgController @Autowired constructor(private val repo: OrgRepository) {
    @GetMapping("")
    fun getAll() = repo.findAll()
}
