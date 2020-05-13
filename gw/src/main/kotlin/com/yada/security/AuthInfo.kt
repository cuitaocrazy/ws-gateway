package com.yada.security


import com.yada.web.model.Res
import com.yada.web.model.User

data class AuthInfo(val user: User, val resList: List<Res>) {
    fun hasRole(roleId: String) = user.roles.any { it == roleId }
    fun hasSvc(svcId: String) = resList.any { it.uri.startsWith("""/{$svcId}/""", true) }
}

//class AuthInfo(private val claims: Claims) : Claims by claims, Principal {
//    companion object {
//        fun create(user: User, resList: List<Res>) = AuthInfo().apply {
//            isAdmin = false
//            this.user = user
//            this.resList = resList
//            this.username = user.id
//        }
//
//        fun create(adminName: String) = AuthInfo().apply { isAdmin = true; username = adminName }
//    }
//
//    constructor() : this(DefaultClaims())
//
//    var isAdmin: Boolean
//        get() = this["isAdmin"] as Boolean
//        set(value) {
//            this["isAdmin"] = value
//        }
//
//    var user: User?
//        get() = (this["userInfo"] as? Map<*, *>)?.run(::convertUser)
//        set(value) {
//            this["userInfo"] = value
//        }
//
//    var resList: List<Res>?
//        get() = (this["resList"] as? List<*>)
//                ?.filterIsInstance<Map<*, *>>()
//                ?.map(::convertRes)
//        set(value) {
//            this["resList"] = value
//        }
//    var username: String?
//        get() = this.subject
//        set(value) {
//            this.subject = value
//        }
//
//    override fun getName(): String? = this.username
//}
//
//private fun convertUser(map: Map<*, *>): User =
//        User(
//                map["id"] as String, map["orgId"] as String,
//                (map["roles"] as List<*>).filterIsInstance<String>().toSet()
//        )
//
//private fun convertRes(map: Map<*, *>): Res =
//        Res(map["uri"] as String, (map["ops"] as List<*>)
//                .filterIsInstance<String>().map(::convertOp).toSet())
//
//private fun convertOp(opStr: String): Operator = Operator.valueOf(opStr)

// https://medium.com/holisticon-consultants/kotlin-data-class-mapping-aa0f9f750ca1
// 方法不错，改动了一下，但是用到了反射，并且没有缓存，现在的结构也简单，备用下

//typealias Mapper<I, O> = (I) -> O
//
//class ObjectMapper<O : Any>(outType: KClass<O>) : Mapper<Map<String, *>, O> {
//    companion object {
//        inline operator fun <reified T : Any> invoke() = ObjectMapper(T::class)
//        fun <O : Any> listMapper(mapper: Mapper<Map<String, *>, O>): Mapper<List<Map<String, *>>, List<O>> = { data -> data.map(mapper) }
//    }
//
//    private val outConstructor = outType.primaryConstructor!!
//    val fieldMappers = mutableMapOf<String, Mapper<Any, Any>>()
//    private fun argFor(parameter: KParameter, data: Map<String, *>): Any? {
//        val value = data[parameter.name] ?: return null
//
//        return fieldMappers[parameter.name]?.invoke(value) ?: value
//    }
//
//    inline fun <reified S : Any, reified T : Any> register(parameterName: String, crossinline mapper: Mapper<S, T>): ObjectMapper<O> = apply {
//        this.fieldMappers[parameterName] = { data -> mapper(data as S) }
//    }
//
//    override fun invoke(data: Map<String, *>): O = with(outConstructor) {
//        callBy(parameters.associateWith { argFor(it, data) })
//    }
//
//}

//val userOm = ObjectMapper<User>().register("roles") { data: List<String> -> data.toSet() }
//val resOm = ObjectMapper<Res>().register("ops") { data: List<String> -> data.map { Operator.valueOf(it) }.toSet() }
//val resesOm = ObjectMapper.listMapper(resOm)
