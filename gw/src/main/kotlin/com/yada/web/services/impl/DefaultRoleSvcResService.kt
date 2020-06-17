package com.yada.web.services.impl

import com.yada.web.model.DefaultRoleSvcRes
import com.yada.web.repository.DefaultRoleSvcResRepository
import com.yada.web.services.IDefaultRoleSvcResService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
open class DefaultRoleSvcResService @Autowired constructor(
        private val defaultRoleSvcResRepository: DefaultRoleSvcResRepository
) : IDefaultRoleSvcResService {

    override fun get(): Mono<List<DefaultRoleSvcRes>> = defaultRoleSvcResRepository.findAll().collectList()

    @Transactional
    override fun createOrUpdate(defaultRoleSvcRes: List<DefaultRoleSvcRes>): Mono<List<DefaultRoleSvcRes>> =
            defaultRoleSvcResRepository.deleteAll()
                    .then(defaultRoleSvcResRepository.saveAll(defaultRoleSvcRes).collectList())
}