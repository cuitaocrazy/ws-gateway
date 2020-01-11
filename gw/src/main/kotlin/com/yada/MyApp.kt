package com.yada

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
open class MyApp

@Configuration
@EnableReactiveMongoRepositories
open class MongoConfig : AbstractReactiveMongoConfiguration() {
    override fun reactiveMongoClient() = mongoClient()

    override fun getDatabaseName() = "test"

    override fun reactiveMongoTemplate() = ReactiveMongoTemplate(mongoClient(), databaseName)

    @Bean
    open fun mongoClient(): MongoClient = MongoClients.create()

}

fun main(args: Array<String>) {
    runApplication<MyApp>(*args)
}