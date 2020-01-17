package com.yada

import com.yada.services.IOrgService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

//@DataMongoTest
@SpringBootTest
class MyTest @Autowired constructor(private val orgSvc: IOrgService) {

    @Test
    fun t() {
        val tree = orgSvc.getTree("")
        tree.subscribe(::println)
        tree.blockLast()
    }
}
