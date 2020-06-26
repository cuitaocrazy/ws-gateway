package com.yada.security.web

//class AuthContextFilter(private val auth: Auth) : WebFilter {
//    companion object {
//        private const val authCookiesKey = "AUTH_ID"
//    }
//
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//        val token = exchange.request.cookies[authCookiesKey]?.run { this[0]?.value }
//
//        val setToken = { _token: String ->
//            val cookie = ResponseCookie.from(authCookiesKey, _token).build()
//            exchange.response.addCookie(cookie)
//        }
//
//        exchange.response.beforeCommit {
//            Mono.subscriberContext().flatMap { ctx ->
//                ctx.getOrEmpty<String>(tokenKey).map { token ->
//                    setToken(token)
//                    auth.refreshToken(token)
//                }.orElse(Mono.empty())
//            }
//        }
//
//
//        return chain.filter(exchange).subscriberContext { ctx ->
//            ctx.apply {
//                put(authKey, auth)
//                if (token != null) {
//                    put(tokenKey, token)
//                }
//                put(sendTokenFnKey, setToken)
//            }
//        }
//    }
//}
