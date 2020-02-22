package com.yada

import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories
open class MongoConfig constructor(
        @Value("\${yada.db.mongo.db:yada_auth}")
        private val dbName: String,
        @Value("\${yada.db.mongo.url:mongodb://localhost/?replicaSet=rs}")
        private val url: String
) : AbstractReactiveMongoConfiguration() {
    override fun reactiveMongoClient() = mongoClient()

    override fun getDatabaseName() = dbName

    override fun reactiveMongoTemplate() = ReactiveMongoTemplate(mongoClient(), databaseName)

    @Bean
    open fun mongoClient(): MongoClient = MongoClients.create(ConnectionString(url))

    @Bean
    open fun transactionManager(factory: ReactiveMongoDatabaseFactory) = ReactiveMongoTransactionManager(factory)

    @Bean
    open fun messageSource(): MessageSource? {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasenames("languages/messages")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }
}