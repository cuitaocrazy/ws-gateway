package com.yada.services

import com.yada.JwtTokenUtil
import com.yada.model.App
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
    fun getUserResListByApp(appId: String, user: User): Mono<List<Res>>
    fun getUserResList(user: User): Mono<List<Res>>
}

private fun svcUri(svcId: String, uri: String) = "/${svcId}${uri}"

@Service
class AuthorizationService @Autowired constructor(
        private val appService: AppService,
        private val jwtUtil: JwtTokenUtil) : IAuthorizationService {

    override fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean> =
            Mono.just(jwtUtil.getEntity(token)!!.resList!!.any { it.uri == uri && opt in it.ops })

    override fun getUserResListByApp(appId: String, user: User): Mono<List<Res>> =
            appService.get(appId).map(getResList(user)).map(this::mergeRes)

    override fun getUserResList(user: User): Mono<List<Res>> =
            appService.getAll().map(getResList(user)).reduce { s, e -> s + e }.map(this::mergeRes)

    private fun getResList(user: User) = fun(app: App): List<Res> {
        val userRoles = user.roles.filter { it.appId == app.id }.map { it.roleName }
        // 角色名列表
        val roles = app.roles.filter { it.name in userRoles }
        // Res列表
        return roles.flatMap { it.resources.flatMap { svc -> svc.resources.map { res -> Res(svcUri(svc.id, res.uri), res.ops) } } }
    }

    // 合并相同uri的ops
    private fun mergeRes(resList: List<Res>) = resList.groupBy { it.uri }.map { entry -> Res(entry.key, entry.value.flatMap { it.ops }.toSet()) }
}