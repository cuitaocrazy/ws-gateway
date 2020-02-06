package com.yada.services

import com.yada.model.*
import com.yada.repository.AppRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IAppService {
    fun getAll(): Flux<App>
    fun get(id: String): Mono<App>
    fun createOrUpdate(app: App): Mono<App>
    fun exist(id: String): Mono<Boolean>
    fun delete(id: String): Mono<Void>
}

@Service
open class AppService constructor(private val appRepository: AppRepository, private val userSvc: IUserService) : IAppService {
    override fun getAll(): Flux<App> = appRepository.findAllByOrderByIdAsc()

    override fun get(id: String): Mono<App> = appRepository.findById(id)

    @Transactional
    override fun createOrUpdate(app: App): Mono<App> = appRepository.findById(app.id).flatMap { oldApp ->
        val newApp = extractApp(app)
        val minusRoleIds = (oldApp.roles.map { it.name } - newApp.roles.map { it.name }).map { RoleId(app.id, it) }
        extractUser(userSvc.getAll(), minusRoleIds).map(userSvc::createOrUpdate).then(Mono.just(newApp))
    }.defaultIfEmpty(app).flatMap { appRepository.save(it) }

    @Transactional
    override fun exist(id: String): Mono<Boolean> = appRepository.existsById(id)

    @Transactional
    override fun delete(id: String): Mono<Void> = get(id).flatMapMany { app ->
        extractUser(userSvc.getAll(), app.roles.map { RoleId(app.id, it.name) }).map(userSvc::createOrUpdate)
    }.then(appRepository.deleteById(id))
}

private val extractUser: (Flux<User>, List<RoleId>) -> Flux<User> = { users, roleIds ->
    // Flux#map当是Fuseable版本的，返回null没问题，并且会自动忽略，但是非Fuseable版本的必须是one to one的，不可返回null，目前发现mongodb给的Flux是非Fuseable版本，因此改为reduce版本的
    users.reduce(listOf<User>()) { s, e ->
        val newRoleIds = e.roles.filter { it !in roleIds }
        if (newRoleIds.size == e.roles.size) s else s + e.copy(roles = newRoleIds.toSet())
    }.flatMapMany { Flux.fromIterable(it) }
//    users.map { u ->
//        val newRoleIds = u.roles.filter { it !in roleIds }
//        if (newRoleIds.size == u.roles.size) null else u.copy(roles = newRoleIds.toSet())
//    }
}

private val extractApp: (App) -> App = { app ->
    val rs = app.roles.map { r -> r.copy(resources = extractSvcReses(r.resources, app.resources)) }
    app.copy(roles = rs.toSet())
}

private val extractSvcReses: (Set<SvcRes>, Set<SvcRes>) -> Set<SvcRes> = { roleSvcReses, svcReses ->
    roleSvcReses.fold(setOf()) { s, e ->
        val svcRes = svcReses.firstOrNull { it.id == e.id }
        if (svcRes == null) s else {
            val newSvcRes = extractSvcRes(e, svcRes)
            if (newSvcRes.resources.isEmpty()) s else s + newSvcRes
        }
    }
}

private val extractSvcRes: (SvcRes, SvcRes) -> SvcRes = { roleSvcRes, svcRes ->
    val newReses = roleSvcRes.resources.fold(setOf<Res>()) { s, e ->
        val res = svcRes.resources.firstOrNull { e.uri == it.uri }
        if (res == null) s else {
            val newRes = extractRes(e, res)
            if (newRes.ops.isEmpty()) s else s + newRes
        }
    }
    roleSvcRes.copy(resources = newReses)
}

private val extractRes: (Res, Res) -> Res = { roleRes, res ->
    val newOps = roleRes.ops.fold(setOf<Operator>()) { s, e ->
        if (e in res.ops) s + e else s
    }
    roleRes.copy(ops = newOps)
}