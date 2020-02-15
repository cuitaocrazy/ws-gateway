package com.yada.controllers

import com.yada.model.Org
import com.yada.services.IOrgService
import com.yada.withNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/apis/org")
class OrgController @Autowired constructor(private val orgService: IOrgService) {
    @GetMapping
    fun getTree(@RequestParam("id_prefix") idPrefix: String?) = orgService.getTree(idPrefix)

    @GetMapping("{id}")
    fun getOrg(@PathVariable("id") id: String) = withNotFound(orgService.get(id))

    @PutMapping
    fun createOrUpdate(@RequestBody org: Org) = orgService.createOrUpdate(org)

    @GetMapping("{id}/exist")
    fun existOrg(@PathVariable("id") id: String) = orgService.exist(id)

    @DeleteMapping("{id}")
    fun deleteOrg(@PathVariable("id") id: String) = orgService.delete(id)


//    @GetMapping("save")
//    fun save(): Flux<Org> = repo.saveAll(listOf(
//            Org("00", "org 00"),
//            Org("0000", "org 0000"),
//            Org("000000", "org 000000"),
//            Org("000001", "org 000001"),
//            Org("0001", "org 0001"),
//            Org("01", "org 01"),
//            Org("0100", "org 0100"),
//            Org("02", "org 02")
//    ))
}
