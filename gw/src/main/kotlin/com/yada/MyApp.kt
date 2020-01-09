package com.yada

import io.r2dbc.h2.H2ConnectionConfiguration
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.core.publisher.Mono

@SpringBootApplication
open class MyApp

//@Configuration
//@EnableR2dbcRepositories
//open class R2dbcConfiguration : AbstractR2dbcConfiguration() {
//    @Bean
//    override fun connectionFactory() =
//        H2ConnectionFactory(
//            H2ConnectionConfiguration.builder()
//                .url("mem:testdb;DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4")
//                .username("sa")
//                .build()
//        )
//}

fun main(args: Array<String>) {
//    SpringApplication.run(MyApp::class.java, *args)
    runApplication<MyApp>(*args)
}