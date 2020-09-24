package com.yada.web.services.impl

import com.yada.security.IPwdDigestService
import com.yada.security.Power
import com.yada.web.model.Res
import com.yada.web.model.User
import com.yada.web.pathPatternParser
import com.yada.web.repository.UserRepository
import com.yada.web.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
open class UserService @Autowired constructor(
        private val userRepo: UserRepository,
        private val pwdDigestService: IPwdDigestService
) : IUserService {
    override fun get(id: String): Mono<User> = userRepo.findById(id)

    override fun getByOrgId(orgId: String): Flux<User> = userRepo.findByOrgIdOrderByIdAsc(orgId)

    @Transactional
    override fun createOrUpdate(user: User): Mono<User> =
            getPwd(user.id)
                    .flatMap { pwd ->
                        userRepo.save(user)
                                .flatMap {
                                    changePwd(user.id, pwd).then(Mono.just(it))
                                }
                    }
                    .switchIfEmpty(
                            userRepo.save(user)
                                    .flatMap {
                                        resetPwd(it.id).then(Mono.just(it))
                                    }
                    )

    @Transactional
    override fun delete(id: String): Mono<Void> = userRepo.deleteById(id)

    @Transactional
    override fun deleteByOrgId(orgId: String): Mono<Void> = userRepo.deleteByOrgId(orgId)

    override fun exist(id: String): Mono<Boolean> = userRepo.existsById(id)

    override fun getPwd(id: String): Mono<String> = userRepo.findPwdById(id)

    @Transactional
    override fun resetPwd(id: String): Mono<Void> = userRepo.changePwd(id, pwdDigestService.getDefaultPwdDigest(id))

    @Transactional
    override fun changePwd(id: String, pwd: String): Mono<Void> = userRepo.changePwd(id, pwd)

    @Transactional
    override fun changePwd(id: String, oldPwd: String, newPwd: String): Mono<Boolean> = getPwd(id)
            .filter { pwdDigestService.checkPwdDigest(id, oldPwd, it) }
            .flatMap {
                changePwd(id, pwdDigestService.getPwdDigest(id, newPwd)).then(Mono.just(true))
            }
            .defaultIfEmpty(false)

    override fun getAll(): Flux<User> = userRepo.findAll()

    override fun filterApis(apiList: List<Res>, userResList: List<Power>): Mono<List<Res>> {
        val resParsers = userResList
                .map { p ->
                    object {
                        val parser = pathPatternParser.parse(p.res)
                        val ops = p.opts
                    }
                }
        val retList = apiList.mapNotNull { apiRes ->
            resParsers.firstOrNull {
                it.parser.matches(PathContainer.parsePath(apiRes.uri))
            }?.run {
                apiRes.copy(ops = apiRes.ops.filter { it in ops }.toSet())
            }
        }

        return Mono.just(retList)
    }
}