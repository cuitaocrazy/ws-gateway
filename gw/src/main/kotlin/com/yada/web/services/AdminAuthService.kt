package com.yada.web.services

import com.yada.security.AuthInfo
import com.yada.security.IPwdDigestService
import com.yada.security.JwtTokenUtil
import com.yada.web.model.Admin
import com.yada.web.model.Operator
import com.yada.web.repository.AdminRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

interface IAdminAuthService {
    fun login(username: String, password: String): Mono<AuthInfo>
    fun logout(token: String): Mono<Void>
    fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean>
    fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean>
}

private const val adminStr = "admin"

// TODO: 创建默认的admin用户比较难受，需要重构
@Service
open class AdminAuthService @Autowired constructor(
        private val adminRepo: AdminRepository,
        private val pwdDigestService: IPwdDigestService,
        private val jwtUtil: JwtTokenUtil
) : IAdminAuthService {
    override fun login(username: String, password: String): Mono<AuthInfo> {
        var monoPwd = adminRepo.findPwdById(username)
        if (username == adminStr)
            monoPwd = monoPwd.defaultIfEmpty(pwdDigestService.getDefaultPwdDigest(adminStr))
        return monoPwd.filter { pwdDigestService.getPwdDigest(username, password) == it }
                .map { AuthInfo.create(username) }
    }

    // 看以后是否管理token，在实现
    override fun logout(token: String): Mono<Void> = Mono.empty()

    override fun authorize(token: String, uri: String, opt: Operator): Mono<Boolean> =
            Mono.just(jwtUtil.getEntity(token) != null)

    @Transactional
    override fun changePassword(username: String, oldPassword: String, newPassword: String): Mono<Boolean> {
        val newPwdDigest = pwdDigestService.getPwdDigest(username, newPassword)
        val oldPwdDigest = pwdDigestService.getPwdDigest(username, oldPassword)

        return adminRepo.existsById(username)
                .flatMap {
                    when {
                        it -> adminRepo.findPwdById(username)
                        username == adminStr -> createAdmin().then(adminRepo.findPwdById(username))
                        else -> Mono.empty()
                    }
                }
                .map { it == oldPwdDigest }
                .filter { it }
                .flatMap {
                    adminRepo.changePwd(username, newPwdDigest)
                            .then(Mono.just(true))
                }
                .defaultIfEmpty(false)
    }

    private fun createAdmin() =
            adminRepo.save(Admin(adminStr))
                    .then(adminRepo.changePwd(adminStr, pwdDigestService.getDefaultPwdDigest(adminStr)))

}