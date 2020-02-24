package com.yada.services

import com.yada.JwtTokenUtil
import com.yada.model.Operator
import com.yada.model.Res
import com.yada.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/***
 * 授权服务接口
 */
interface IAuthorizationService {
    fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean>
    fun getUserResList(user: User): Mono<List<Res>>
}

private fun svcUri(svcId: String, uri: String) = "/${svcId}${uri}"

@Service
class AuthorizationService @Autowired constructor(
        private val roleService: IRoleService,
        private val jwtUtil: JwtTokenUtil) : IAuthorizationService {

    override fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean> =
            Mono.just(jwtUtil.getEntity(token)!!.resList!!.any { it.uri == uri && opt in it.ops })

    override fun getUserResList(user: User): Mono<List<Res>> =
            roleService.getAll()
                    .filter { it.id in user.roles }
                    .map { role -> role.svcs.flatMap { svc -> svc.resources.map { Res(svcUri(svc.id, it.uri), it.ops) } } }
                    .reduce { s, e -> s + e }
                    .map(this::mergeRes)

    // 合并相同uri的ops
    private fun mergeRes(resList: List<Res>) = resList.groupBy { it.uri }.map { entry -> Res(entry.key, entry.value.flatMap { it.ops }.toSet()) }
}