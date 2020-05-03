package com.yada.init

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


/**
 * # 创建全部的collection
 *
 * 我们平常操作mongo时，当做insert没有Collection，mongo就会帮你创建一个Collection，
 * 但在使用mongo多文档事务时要求[在做CRUD时Collection必须存在](https://docs.mongodb.com/manual/core/transactions/), 否则会抛异常
 *
 * 没有找到mongo driver或spring data mongodb相关的设置，网上其他资料也没找到（估计很少人用多文档事务或用初始化脚本）
 *
 * 如果找到好的方法可告诉我
 *
 * 以下方法实现是通过读[ReactiveMongoTemplate]代码临时的一个实现
 *
 * 服务程序里有的Entity集合：通过[MongoMappingContext][org.springframework.data.mongodb.core.mapping]得到
 *
 * 数据库里的Collection集合：通过[ReactiveMongoTemplate]得到
 *
 * 并且比较两个集合创建库中没有的collection, 然后创建
 *
 * # 知识
 *
 * 这次通过代码找到了为什么Entity不标[Document][org.springframework.data.mongodb.core.mapping.Document]和不标扫描路径也能用的原因,
 * 大多归功于继承了[AbstractReactiveMongoConfiguration][org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration],
 * **但是不能省去，可能出现嵌套类型，他就不知道那些是root，那些是child，出现意想不到的结果**
 *
 * [MongoMappingContext][org.springframework.data.mongodb.core.mapping]加载Entity：
 * 1. [AbstractReactiveMongoConfiguration][org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration]确定了扫描路径
 * 2. 先扫描有相关注解的
 * 3. 扫描repository相关的Entity（原因)
 *
 */
fun initMongoDbCollection(reactiveMongoTemplate: ReactiveMongoTemplate): Mono<Void> {
    // 只要标记Document的Entity，简单， 也符合规范，不想去找Repository挂的泛型
    val allEntities = reactiveMongoTemplate.converter.mappingContext.persistentEntities
            .filter { it.findAnnotation(Document::class.java) != null }
    val resolver = MongoPersistentEntityIndexResolver(reactiveMongoTemplate.converter.mappingContext)

    return reactiveMongoTemplate.collectionNames.collectList().map { serverCollections ->
        allEntities.filter { it.collection !in serverCollections }
    }.flatMap { entities ->
        val createCollection = entities.map {
            reactiveMongoTemplate.createCollection(it.collection)
        }.let { Flux.merge(it) }.collectList()

        val createIndex = entities.flatMap { entity ->
            val opt = reactiveMongoTemplate.indexOps(entity.collection)
            resolver.resolveIndexForEntity(entity).map(opt::ensureIndex)
        }.let { Flux.merge(it) }.collectList()

        createCollection.then(createIndex)
    }.then()
}