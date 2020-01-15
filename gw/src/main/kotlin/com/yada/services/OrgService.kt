package com.yada.services

import com.yada.model.Org
import com.yada.repository.OrgRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class OrgTree(val org: Org, val children: Set<OrgTree>?)

interface IOrgService {
    fun getTree(orgIdPrefix: String?): Flux<OrgTree>
    fun createOrUpdate(org: Org): Mono<Org>
    fun delete(id: String): Mono<Void>
    fun get(id: String): Mono<Org>
    fun exist(id: String): Mono<Boolean>
}

fun Org.isMyOffspring(org: Org) = org.id.startsWith(this.id)
//fun OrgTree.toList(): List<Org> {
//    fun Set<OrgTree>.toList() = this.map { it.toList() }.flatten()
//
//    return listOf(org) + (children?.toList() ?: listOf())
//}

private fun makeTree(orgs: List<Org>): List<OrgTree> {
    val ret = ArrayList<OrgTree>()
    var tmp = orgs
    while (tmp.isNotEmpty()) {
        val o = tmp.first()
        val childrenOrgTree = tmp.drop(1).partition(o::isMyOffspring).run {
            tmp = second
            if (first.isEmpty()) null else makeTree(first).toSet()
        }

        ret.add(OrgTree(o, childrenOrgTree))
    }
    return ret
}

@Service
class OrgService @Autowired constructor(private val repo: OrgRepository) : IOrgService {
    override fun getTree(orgIdPrefix: String?): Flux<OrgTree> =
            repo.findByRegexId("^${orgIdPrefix ?: ""}.*")
                    .collectList()
                    .map(::makeTree)
                    .flatMapMany { Flux.fromIterable(it) }

    override fun createOrUpdate(org: Org): Mono<Org> = repo.save(org)

    override fun delete(id: String): Mono<Void> = repo.deleteById(id)

    override fun get(id: String): Mono<Org> = repo.findById(id)

    override fun exist(id: String): Mono<Boolean> = repo.existsById(id)

}
