package com.yada

import com.yada.model.Res
import com.yada.model.User
import io.jsonwebtoken.*
import io.jsonwebtoken.impl.DefaultClaims
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 当[m]没有数据时返回404
 */
fun <T> withNotFound(m: Mono<T>, msg: String? = null): Mono<T> = m.switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, msg)))

/**
 * 网上抄的一个kotlin通用logger实现
 * 用的是第三种方法，属性委托
 * 网址：https://www.reddit.com/r/Kotlin/comments/8gbiul/slf4j_loggers_in_3_ways/
 */
class LoggerDelegate : ReadOnlyProperty<Any?, Logger> {
    /**
     * 匿名的伴生对象
     */
    companion object {
        private fun <T> createLogger(clazz: Class<T>): Logger {
            return LoggerFactory.getLogger(clazz)
        }
    }

    private var logger: Logger? = null

    /**
     * 重写取值方法。
     * [thisRef]为被委托属性的所在对象引用，[property]为被委托属性的元数据
     */
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        if (logger == null) {
            logger = createLogger(thisRef!!.javaClass)
        }
        return logger!!
    }
}

/**
 * 参考：https://dzone.com/articles/spring-boot-security-json-web-tokenjwt-hello-world
 */
private const val TOKEN_EXPIRE_INTERVAL: Long = 1 * 60 * 60 // 单位：秒

@Component
class TimeUtil {
    fun getCurrentDate() = Date()
}

@Component
class JwtTokenUtil @Autowired constructor(@Value("\${jwt.secret:yadajwt}") val secret: String, val timeUtil: TimeUtil) {
    private fun Date.getExpirationDate() = Date(this.time + TOKEN_EXPIRE_INTERVAL * 1000)
    private fun JwtBuilder.generateToken() = this.signWith(SignatureAlgorithm.HS512, secret).compact()
    private val emptyCookie = ResponseCookie.from("token", "").path(getPath(false)).maxAge(0).build()
    private val adminEmptyCookie = ResponseCookie.from("token", "").path(getPath(true)).maxAge(0).build()

    fun getEmptyCookie(isAdmin: Boolean? = false): ResponseCookie = if(isAdmin == true) adminEmptyCookie else emptyCookie

    fun getEntity(token: String) =
            try {
                JwtEntity(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body)
            } catch (_: SignatureException) {
                null
            } catch (_: ExpiredJwtException) {
                null
            }

    fun generateToken(entity: JwtEntity, currentDate: Date = timeUtil.getCurrentDate()): String =
            Jwts.builder().setClaims(entity).setIssuedAt(currentDate).setExpiration(currentDate.getExpirationDate()).generateToken()

    fun generateCookie(token: String, currentDate: Date = timeUtil.getCurrentDate()): ResponseCookie {
        val entity = getEntity(token)!!
        return generateCookie(token, Duration.ofMillis(entity.expiration.time - currentDate.time), getPath(entity.isAdmin))
    }

    fun renewCookie(token: String, currentDate: Date = timeUtil.getCurrentDate()): ResponseCookie {
        val entity = getEntity(token)!!
        val newToken = Jwts.builder().setClaims(entity).setExpiration(currentDate.getExpirationDate()).generateToken()
        return generateCookie(newToken, Duration.ofMillis(TOKEN_EXPIRE_INTERVAL * 1000), getPath(entity.isAdmin))
    }

    private fun generateCookie(token: String, maxAge: Duration, path: String) = ResponseCookie.from("token", token)
            .maxAge(maxAge)
            .path(path)
            .build()

    private fun getPath(isAdmin: Boolean?) = if (isAdmin == true) "/admin" else "/"


}

class JwtEntity(private val claims: Claims) : Claims by claims {
    constructor() : this(DefaultClaims())

    var isAdmin: Boolean?
        get() = this["isAdmin"] as Boolean?
        set(value) {
            this["isAdmin"] = value
        }

    var user: User?
        get() = this["userInfo", User::class.java]
        set(value) {
            this["userInfo"] = value
        }

    var resList: List<Res>?
        get() = this["resList", List::class.java] as List<Res>
        set(value) {
            this["resList"] = value
        }

    var username: String?
        get() = this.subject
        set(value) {
            this.subject = value
        }
}