package com.yada

import com.yada.model.Org
import com.yada.services.IOrgService
import com.yada.services.OrgTree
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier


@SpringBootTest
class MyTest2 @Autowired constructor(private val orgSvc: IOrgService) {
    private val logger by LoggerDelegate()
    @Test
    fun t() {
        val m = orgSvc.createOrUpdate(Org("00", "org 00"))
        StepVerifier.create(m).expectNext(Org("00", "org 00")).verifyComplete()
        StepVerifier.create(orgSvc.getTree(null))
                .expectNext(OrgTree(Org("00", "org 00"), null)).verifyComplete()
    }
}
