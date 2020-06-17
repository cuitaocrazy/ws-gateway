//package com.yada.web.services
//
//import com.yada.security.TokenManager
//import com.yada.web.model.Operator
//import com.yada.web.model.Res
//import com.yada.web.model.Svc
//import com.yada.web.model.User
//import com.yada.web.pathPatternParser
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.server.PathContainer
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
///***
// * 授权服务接口
// */
//interface IAuthorizationService {
//    fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean>
//    fun getUserResList(user: User): Mono<List<Res>>
//    fun filterApis(apiList: List<Res>, userResList: List<Res>): Mono<List<Res>>
//}
//
//private fun svcUri(svcId: String, uri: String) = "/${svcId}${uri}"
//
//@Service
//class AuthorizationService @Autowired constructor(
//        private val roleService: IRoleService,
//        private val tokenManager: TokenManager,
//        private val defaultRoleSvcResService: DefaultRoleSvcResService
//) : IAuthorizationService {
//
//    override fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean> =
//            tokenManager.get(token).map { authInfo ->
//                authInfo.resList.any { it.uri == uri && opt in it.ops }
//            }
//
//    override fun getUserResList(user: User): Mono<List<Res>> =
//            roleService.getAll()
//                    .map { roles ->
//                        roles.filter { it.id in user.roles }
//                    }
//                    .flatMap { roles ->
//                        val svcList = roles.flatMap { role ->
//                            role.svcs
//                        }
//                        // 用户角色服务资源+默认角色服务资源
//                        defaultRoleSvcResService.get().map { dSvcList ->
//                            dSvcList.map { Svc(it.id, it.resources) } + svcList
//                        }
//                    }.map { svcList ->
//                        val allResList = svcList.flatMap { svc ->
//                            svc.resources.map {
//                                // 转换资源uri带上svcId前缀
//                                Res(svcUri(svc.id, it.uri), it.ops)
//                            }
//                        }
//                        mergeRes(allResList)
//                    }
//
//    override fun filterApis(apiList: List<Res>, userResList: List<Res>): Mono<List<Res>> {
//        val resParsers = userResList
//                .map { res ->
//                    object {
//                        val parser = pathPatternParser.parse(res.uri)
//                        val ops = res.ops
//                    }
//                }
//        val retList = apiList.mapNotNull { apiRes ->
//            resParsers.firstOrNull {
//                it.parser.matches(PathContainer.parsePath(apiRes.uri))
//            }?.run {
//                apiRes.copy(ops = apiRes.ops.filter { it in ops }.toSet())
//            }
//        }
//
//        return Mono.just(retList)
//    }
//
//    // 合并相同uri的ops
//    private fun mergeRes(resList: List<Res>) =
//            resList.groupBy { it.uri }
//                    .map { entry ->
//                        Res(entry.key, entry.value.flatMap { it.ops }.toSet())
//                    }
//
//}