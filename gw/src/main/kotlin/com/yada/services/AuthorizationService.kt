package com.yada.services

import com.yada.JwtTokenUtil
import com.yada.model.Operator
import com.yada.model.Res
import com.yada.model.User
import com.yada.pathPatternParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/***
 * 授权服务接口
 */
interface IAuthorizationService {
    fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean>
    fun getUserResList(user: User): Mono<List<Res>>
    fun filterApis(apiList: List<Res>, userResList: List<Res>): Mono<List<Res>>
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

    override fun filterApis(apiList: List<Res>, userResList: List<Res>): Mono<List<Res>> {
        val resParsers = userResList
                .map { res ->
                    object {
                        val parser = pathPatternParser.parse(res.uri)
                        val ops = res.ops
                    }
                }
        val retList = apiList.mapNotNull { apiRes ->
            resParsers.firstOrNull { it.parser.matches(PathContainer.parsePath(apiRes.uri)) }?.run {
                apiRes.copy(ops = apiRes.ops.filter { it in ops }.toSet())
            }
        }

        return Mono.just(retList)
    }

    // 合并相同uri的ops
    private fun mergeRes(resList: List<Res>) = resList.groupBy { it.uri }.map { entry -> Res(entry.key, entry.value.flatMap { it.ops }.toSet()) }

}