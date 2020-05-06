package com.yada.config

import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

/**
 * # MongoDb自动配置
 *
 * 在创建这个项目的时候Spring Data MongoDb Reactive并没有Auto config, 现在本版升级后有了:[MongoReactiveAutoConfiguration][org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration]
 *
 * 配置的参数参阅[MongoProperties][org.springframework.boot.autoconfigure.mongo.MongoProperties]
 *
 * 如果改为默认的auto config， 注意：[ReactiveMongoTransactionManager]的bean还是要自己写一下
 *
 * 至于[EnableReactiveMongoRepositories][ org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories]这个注解经过修改就不写了，直接默认main的包。这个和[ReactiveMongoContext][org.springframework.data.mongodb.core.ReactiveMongoContext],
 * 如果想弄清楚配置`logging.level.org.springframework=TRACE`进行跟踪就可一目了然
 *
 * 然后就是[ReactiveMongoPersistentEntityIndexCreator][org.springframework.data.mongodb.core.index.ReactiveMongoPersistentEntityIndexCreator]的警告：
 *
 * 当Entity有[Document][org.springframework.data.mongodb.core.mapping.Document]和[Indexed][org.springframework.data.mongodb.core.index]注解时, 现在的版本会自动创建索引， 但是会提示`Automatic index creation will be disabled by default as of Spring Data MongoDB 3.x. Please use 'MongoMappingContext#setAutoIndexCreation(boolean)' or override 'MongoConfigurationSupport#autoIndexCreation()' to be explicit.`
 * 当升级到3.0时需要注意，这个选项到那时默认值时`false`
 *
 */
@Configuration
open class MongoConfig constructor(
        @Value("\${yada.db.mongo.db:yada_auth}")
        private val dbName: String,
        @Value("\${yada.db.mongo.url:mongodb://localhost/?replicaSet=rs}")
        private val url: String
) : AbstractReactiveMongoConfiguration() {
    override fun reactiveMongoClient(): MongoClient = MongoClients.create(ConnectionString(url))

    override fun getDatabaseName() = dbName

    @Bean
    open fun transactionManager(factory: ReactiveMongoDatabaseFactory) = ReactiveMongoTransactionManager(factory)

    override fun autoIndexCreation(): Boolean {
        return false
    }
}