package com.yada.controllers

import com.yada.model.Svc
import com.yada.services.SvcService
import com.yada.withNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/apis/svc")
class SvcController @Autowired constructor(private val svcService: SvcService) {
    @GetMapping
    fun getTree() = svcService.getAll()

    @GetMapping("{id}")
    fun getSvc(@PathVariable("id") id: String) = withNotFound(svcService.get(id))

    @PutMapping
    fun createOrUpdate(@RequestBody svc: Svc) = svcService.createOrUpdate(svc)

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) = svcService.delete(id)
}
