package com.yada.config

import com.hazelcast.client.HazelcastClient
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * # Hazelcast配置
 *
 * Hazelcast作为token管理有的好处：
 * 1. 支持TTL
 * 2. 去中心化
 *
 * 目前不理想的是减慢的spring boot的起停速度
 */
@Configuration
open class HazelcastConfig {
    @PostConstruct
    open fun init() {
        // TODO：默认的配置，不安全，如果只是内环，可忽略，不是就需要加入用户名密码配置，总之需要配置，没想好用spring还是他自己的xml文件
        Hazelcast.newHazelcastInstance()
    }

    @Bean
    open fun client(): HazelcastInstance = HazelcastClient.newHazelcastClient()

    @Bean
    open fun tokenMap(): IMap<String, String> = client().getMap("tokens")

    @PreDestroy
    open fun destroy() {
        HazelcastClient.shutdownAll()
        Hazelcast.shutdownAll()
    }
}