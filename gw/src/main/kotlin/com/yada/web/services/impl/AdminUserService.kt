package com.yada.web.services.impl

import com.yada.security.IPwdDigestService
import com.yada.web.model.Admin
import com.yada.web.repository.AdminRepository
import com.yada.web.services.IAdminUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AdminUserService @Autowired constructor(private val adminRepository: AdminRepository, private val pwdDigestService: IPwdDigestService) : IAdminUserService {
    override fun createUser(id: String): Mono<Admin> = adminRepository.save(Admin(id)).flatMap { admin ->
        adminRepository.changePwd(id, pwdDigestService.getDefaultPwdDigest(id)).map { admin }
    }

    override fun deleteUser(id: String): Mono<Void> = adminRepository.deleteById(id)

    override fun changePwd(id: String, oldPwd: String, newPwd: String): Mono<Boolean> = adminRepository.findPwdById(id)
            .filter { pwdDigestService.checkPwdDigest(id, oldPwd, it) }
            .flatMap {
                adminRepository.changePwd(id, pwdDigestService.getPwdDigest(id, newPwd)).then(Mono.just(true))
            }
            .defaultIfEmpty(false)
}