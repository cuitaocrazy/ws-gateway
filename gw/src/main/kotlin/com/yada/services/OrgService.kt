package com.yada.services

import com.yada.model.Org
import com.yada.repository.OrgRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class OrgTree(val org: Org, val children: Set<OrgTree>?)

interface IOrgService {
    fun getTree(orgId: String?): Flux<OrgTree>
    fun getChildren(orgId: String?): Flux<Org>
    fun create(id: String, name: String): Mono<Org?>
    fun changeName(id: String, name: String): Mono<Org?>
    fun delete(id: String): Mono<Void>
    fun get(id: String): Mono<Org?>
    fun exist(id: String): Mono<Boolean>
}

fun Org.isMyOffspring(org: Org) = org.id.startsWith(this.id)
//fun OrgTree.toList(): List<Org> {
//    fun Set<OrgTree>.toList() = this.map { it.toList() }.flatten()
//
//    return listOf(org) + (children?.toList() ?: listOf())
//}

fun makeTree(orgs: List<Org>): List<OrgTree> {
    val ret = ArrayList<OrgTree>()
    var tmp = orgs
    while (tmp.isNotEmpty()) {
        val o = tmp.first()
        val childrenOrgTree = tmp.drop(1).partition { o.isMyOffspring(it) }.run {
            tmp = second
            if (first.isEmpty()) null else makeTree(first).toSet()
        }

        ret.add(OrgTree(o, childrenOrgTree))
    }
    return ret
}

@Service
class OrgService @Autowired constructor(private val repo: OrgRepository) : IOrgService {
    override fun getTree(orgId: String?): Flux<OrgTree> =
            repo.findByRegexId("^${orgId ?: ""}.*")
                    .reduceWith({ emptyList<Org>() }) { s, e -> s + e }
                    .map(::makeTree)
                    .flatMapMany { Flux.fromIterable(it) }

    override fun getChildren(orgId: String?): Flux<Org> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun create(id: String, name: String): Mono<Org?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeName(id: String, name: String): Mono<Org?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: String): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(id: String): Mono<Org?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exist(id: String): Mono<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}