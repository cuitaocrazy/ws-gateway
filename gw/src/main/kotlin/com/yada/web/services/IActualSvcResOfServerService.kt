package com.yada.web.services

import com.yada.web.model.Res
import reactor.core.publisher.Mono

interface IActualSvcResOfServerService {
    fun get(svcId: String): Mono<List<Res>>
    fun getAllSvcId(): List<String>
}
