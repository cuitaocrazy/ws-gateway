package com.yada.auths

import com.yada.JwtEntity
import com.yada.JwtTokenUtil
import com.yada.model.*
import com.yada.services.AppService
import com.yada.services.IPwdDigestService
import com.yada.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

/**
 * 认帐服务接口
 */
interface IAuthenticationService {
    fun login(username: String, password: String): Mono<String>
    fun logout(token: String): Mono<Void>
    fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean>
}

/***
 * 授权服务接口
 */
interface IAuthorizationService {
    fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean>
    fun getUserResListByApp(appId: String, user: User): Mono<List<Res>>
    fun getUserResList(user: User): Mono<List<Res>>
}

@Service
class AuthenticationService @Autowired constructor(
        private val userService: IUserService,
        private val pwdDigestService: IPwdDigestService,
        private val jwtUtil: JwtTokenUtil,
        private val author: IAuthorizationService) : IAuthenticationService {
    override fun login(username: String, password: String): Mono<String> =
            userService.getPwd(username).map { it == pwdDigestService.getPwdDigest(username, password) }.filter { it }
                    .flatMap { userService.get(username) }
                    .flatMap { user ->
                        author.getUserResList(user).map { resList ->
                            jwtUtil.generateToken(JwtEntity().apply { this.username = user.id; this.user = user; this.resList = resList })
                        }
                    }

    override fun logout(token: String): Mono<Void> = Mono.empty()

    override fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean> =
            userService.getPwd(username)
                    .map { it == pwdDigestService.getPwdDigest(username, oldPassword) }
                    .filter { it }
                    .flatMap {
                        userService.changePwd(username, pwdDigestService.getPwdDigest(username, newPassword))
                    }
                    .map { true }
                    .defaultIfEmpty(false)
}

private fun svcUri(svcId: String, uri: String) = "/svc/${svcId}${uri}"

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

/***
 * 授权认证过滤器
 */
@Component
class AuthSiteFilter @Autowired constructor(private val jwtUtil: JwtTokenUtil) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        return when {
            path.startsWith("/admin/apis") -> adminFilter(exchange, chain)
            path.startsWith("/app") -> appFilter(exchange, chain)
            path.startsWith("/svc") -> svcFilter(exchange, chain)
            else -> chain.filter(exchange)
        }
    }

    private fun adminFilter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = getToken(exchange)
        val entity = token?.run { jwtUtil.getEntity(this) }

        return if(entity != null && entity.isAdmin as Boolean) {
            exchange.response.addCookie(jwtUtil.renewCookie(token))
            chain.filter(exchange)
        } else {
            exchange.response.addCookie(jwtUtil.getEmptyCookie(true))
            Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"))
        }

    }

    private fun appFilter(exchange: ServerWebExchange, chain: WebFilterChain) = commonFilter(exchange, chain) { exch, _ ->
        exch.response.statusCode = HttpStatus.MOVED_PERMANENTLY
        exch.response.headers.location = UriComponentsBuilder.fromPath("/login").queryParam("redirect", exch.request.uri).build().encode().toUri()
        Mono.empty()
    }

    private fun svcFilter(exchange: ServerWebExchange, chain: WebFilterChain) = commonFilter(exchange, chain) { _, _ ->
        Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"))
    }

    /**
     * 每次请求，并且不管是不是静态资源都会刷新token和cookies的效期，合理性待以后思考
     */
    private fun commonFilter(exchange: ServerWebExchange, chain: WebFilterChain, rejectProcess: (ServerWebExchange, WebFilterChain) -> Mono<Void>): Mono<Void> {
        val token = getToken(exchange)

        return if (token != null && jwtUtil.getEntity(token) != null) {
            exchange.response.addCookie(jwtUtil.renewCookie(token))
            chain.filter(exchange)
        } else {
            if (token != null)
                exchange.response.addCookie(jwtUtil.getEmptyCookie())
            rejectProcess(exchange, chain)
        }
    }

    private fun getToken(exchange: ServerWebExchange) = exchange.request.cookies["token"]?.run { this[0]?.value }
}
