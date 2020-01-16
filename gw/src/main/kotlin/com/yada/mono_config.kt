package com.yada

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories
open class MongoConfig : AbstractReactiveMongoConfiguration() {
    override fun reactiveMongoClient() = mongoClient()

    override fun getDatabaseName() = "test"

    override fun reactiveMongoTemplate() = ReactiveMongoTemplate(mongoClient(), databaseName)

    @Bean
    open fun mongoClient(): MongoClient = MongoClients.create()

    @Bean
    open fun myRoutes(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route{ predicateSpec ->
        predicateSpec.path("/get").filters{ gatewayFilterSpec ->
            gatewayFilterSpec.addRequestHeader("Hello", "World")
        }.uri("http://httpbin.org:80")
    }.build()

}