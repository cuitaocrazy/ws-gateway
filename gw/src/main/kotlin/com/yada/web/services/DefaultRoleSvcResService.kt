package com.yada.web.services

import com.yada.web.model.DefaultRoleSvcRes
import com.yada.web.repository.DefaultRoleSvcResRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

interface IDefaultRoleSvcResService {
    fun get(): Mono<List<DefaultRoleSvcRes>>
    fun createOrUpdate(defaultRoleSvcRes: List<DefaultRoleSvcRes>): Mono<List<DefaultRoleSvcRes>>
}

@Service
class DefaultRoleSvcResService @Autowired constructor(
        private val defaultRoleSvcResRepository: DefaultRoleSvcResRepository
) : IDefaultRoleSvcResService {

    override fun get(): Mono<List<DefaultRoleSvcRes>> = defaultRoleSvcResRepository.findAll().collectList()

    @Transactional
    override fun createOrUpdate(defaultRoleSvcRes: List<DefaultRoleSvcRes>): Mono<List<DefaultRoleSvcRes>> =
            defaultRoleSvcResRepository.deleteAll()
                    .then(defaultRoleSvcResRepository.saveAll(defaultRoleSvcRes).collectList())
}