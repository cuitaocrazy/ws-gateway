package com.yada

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 网上抄的一个kotlin通用logger实现
 * 用的是第三种方法，属性委托
 * 网址：https://www.reddit.com/r/Kotlin/comments/8gbiul/slf4j_loggers_in_3_ways/
 */
class LoggerDelegate : ReadOnlyProperty<Any?, Logger> {
    /**
     * 匿名的伴生对象
     */
    companion object {
        private fun <T> createLogger(clazz: Class<T>): Logger {
            return LoggerFactory.getLogger(clazz)
        }
    }

    private var logger: Logger? = null

    /**
     * 重写取值方法。
     * [thisRef]为被委托属性的所在对象引用，[property]为被委托属性的元数据
     */
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        if (logger == null) {
            logger = createLogger(thisRef!!.javaClass)
        }
        return logger!!
    }
}

//private const val TOKEN_EXPIRE_INTERVAL: Long = 1 * 60 * 60 // 单位：秒

@Component
class TimeUtil {
    fun getCurrentDate() = Date()
}

const val adminCollectionName = "admin"

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
 * 大多归功于继承了[AbstractReactiveMongoConfiguration][org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration]
 *
 * [MongoMappingContext][org.springframework.data.mongodb.core.mapping]加载Entity：
 * 1. [AbstractReactiveMongoConfiguration][org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration]确定了扫描路径
 * 2. 先扫描有相关注解的
 * 3. 扫描repository相关的Entity（原因)
 *
 */
fun createAllMongoDbCollection(reactiveMongoTemplate: ReactiveMongoTemplate): Mono<Void> {
    val colTypes = reactiveMongoTemplate.converter
            .mappingContext
            .persistentEntities
            .map { it.typeInformation.type }

    return reactiveMongoTemplate.collectionNames
            .collectList().map { serverCollections ->
                colTypes.filter { reactiveMongoTemplate.getCollectionName(it) !in serverCollections }
            }
            .flatMap { types ->
                // 当标记了Document并且repository也用的Entity，会出现两个相同的type，但hash不同的两个实例, 因此需要去重
                // 应该优先带Document的，这个可能有名字
                Flux.merge(types.distinctBy { it.name }.map { reactiveMongoTemplate.createCollection(it) }).then()
            }
//    val a = colTypes.map { reactiveMongoTemplate.getCollectionName(it) }.filter { it !in reactiveMongoTemplate.collectionNames }
}