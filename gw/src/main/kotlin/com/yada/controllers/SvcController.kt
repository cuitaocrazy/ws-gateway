package com.yada.controllers

import com.yada.repository.SvcRepository
import com.yada.services.ISvcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/svc")
class SvcController @Autowired constructor(private val repo: SvcRepository) {
}