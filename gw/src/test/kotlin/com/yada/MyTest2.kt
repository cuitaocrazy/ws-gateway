package com.yada

import com.yada.services.IAppService
import com.yada.services.IOrgService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.gateway.route.RouteLocator
import java.lang.RuntimeException

//@DataMongoTest
@SpringBootTest
class MyTest2 @Autowired constructor(private val orgSvc: IOrgService) {

    @Test
    fun t() {
        val tree = orgSvc.getTree("")
        tree.subscribe(::println)
        tree.blockLast()
    }
}
