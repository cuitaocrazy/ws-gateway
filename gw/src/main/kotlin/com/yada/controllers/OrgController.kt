package com.yada.controllers

import com.yada.model.Org
import com.yada.repository.OrgRepository
import com.yada.services.IOrgService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/org")
class OrgController @Autowired constructor(private val repo: OrgRepository, private val svc: IOrgService) {
    @GetMapping("")
    fun getAll() = svc.getTree(null)

    @GetMapping("save")
    fun save() = repo.saveAll(listOf(
            Org("00", "org 00"),
            Org("0000", "org 0000"),
            Org("000000", "org 000000"),
            Org("000001", "org 000001"),
            Org("0001", "org 0001"),
            Org("01", "org 01"),
            Org("0100", "org 0100"),
            Org("02", "org 02")
    ))
}
