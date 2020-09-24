package com.yada.web.security

import com.yada.config.TokenManagerCreator
import com.yada.security.AuthWithTokenManager
import com.yada.security.IPwdDigestService
import com.yada.security.Power
import com.yada.security.UserInfo
import com.yada.web.model.Res
import com.yada.web.model.Svc
import com.yada.web.model.User
import com.yada.web.services.IDefaultRoleSvcResService
import com.yada.web.services.IRoleService
import com.yada.web.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GeneralAuth @Autowired constructor(
        private val userService: IUserService,
        private val roleService: IRoleService,
        private val defaultRoleSvcResService: IDefaultRoleSvcResService,
        private val pwdDigestService: IPwdDigestService,
        creator: TokenManagerCreator,
        @Value("\${yada.contextPath:/}")
        private val contextPath: String
) : AuthWithTokenManager({ creator("general") }) {

    override fun checkAndGet(username: String, password: String): Mono<UserInfo> =
            userService.getPwd(username)
                    .filter { pwdDigestService.checkPwdDigest(username, password, it) }
                    .flatMap { userService.get(username) }
                    .flatMap { user ->
                        getUserResList(user).map {
                            UserInfo(user.id, it.map { res -> Power(res.uri, res.ops) }, mapOf("orgId" to user.orgId))
                        }
                    }

    override fun getPath(): String = contextPath

    private fun getUserResList(user: User): Mono<List<Res>> =
            roleService.getAll()
                    .map { roles ->
                        roles.filter { it.id in user.roles }
                    }
                    .flatMap { roles ->
                        val svcList = roles.flatMap { role ->
                            role.svcs
                        }
                        // 用户角色服务资源+默认角色服务资源
                        defaultRoleSvcResService.get().map { dSvcList ->
                            dSvcList.map { Svc(it.id, it.resources) } + svcList
                        }
                    }.map { svcList ->
                        val allResList = svcList.flatMap { svc ->
                            svc.resources.map {
                                // 转换资源uri带上svcId前缀
                                Res(svcUri(svc.id, it.uri), it.ops)
                            }
                        }
                        mergeRes(allResList)
                    }

    private fun mergeRes(resList: List<Res>) =
            resList.groupBy { it.uri }
                    .map { entry ->
                        Res(entry.key, entry.value.flatMap { it.ops }.toSet())
                    }

    private fun svcUri(svcId: String, uri: String) = "/${svcId}${uri}"
}