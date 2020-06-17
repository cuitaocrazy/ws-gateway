package com.yada.web.services.impl

import com.yada.web.model.Role
import com.yada.web.repository.RoleRepository
import com.yada.web.services.IRoleService
import com.yada.web.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
open class RoleService @Autowired constructor(
        private val roleRepo: RoleRepository,
        private val userService: IUserService
) : IRoleService {
    override fun getAll(): Mono<List<Role>> = roleRepo.findAllByOrderByIdAsc().collectList()

    override fun get(id: String): Mono<Role> = roleRepo.findById(id)

    @Transactional
    override fun exist(id: String): Mono<Boolean> = roleRepo.existsById(id)

    @Transactional
    override fun createOrUpdate(role: Role): Mono<Role> = roleRepo.save(role)

    @Transactional
    override fun delete(id: String): Mono<Void> = userService.getAll()
            .filter { user -> id in user.roles }
            .map { user -> user.copy(roles = user.roles - id) }
            .flatMap(userService::createOrUpdate)
            .then(roleRepo.deleteById(id))
}