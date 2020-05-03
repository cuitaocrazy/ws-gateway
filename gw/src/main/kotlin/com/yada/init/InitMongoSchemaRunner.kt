package com.yada.init

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component

@Component
class InitMongoSchemaRunner @Autowired constructor(
        private val reactiveMongoTemplate: ReactiveMongoTemplate
): ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        initMongoDbCollection(reactiveMongoTemplate).block()
    }
}