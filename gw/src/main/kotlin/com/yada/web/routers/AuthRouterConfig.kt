package com.yada.web.routers

import com.yada.security.AuthHolder
import com.yada.security.web.FilterContextBuilder
import com.yada.web.filters.AuthApiHandlerFilter
import com.yada.web.filters.AuthHandlerFilter
import com.yada.web.handlers.AuthHandler
import com.yada.web.security.GeneralAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@Configuration
open class AuthRouterConfig @Autowired constructor(
        private val authHandler: AuthHandler,
        private val authFilter: AuthHandlerFilter,
        private val authApiFilter: AuthApiHandlerFilter,
        private val auth: GeneralAuth
) {
    @Bean
    open fun authRouter() = router {
        "".nest {
            "/login".nest {
                GET("", authHandler::getLoginForm)
                POST("", authHandler::login)
            }
        }

        "".nest {
            GET("/") { _ ->
                ServerResponse.ok().render("/index")
            }
            filter(authFilter)
        }

        "".nest {
            GET("/logout", authHandler::logout)
            POST("/change_pwd", authHandler::changePwd)
            GET("/refresh_token", authHandler::refreshToken)
            GET("/filter_apis", authHandler::filterApis)
            GET("/ui") {
                ok().body(AuthHolder.getUserInfo())
            }
            filter(authApiFilter)
        }

        filter(FilterContextBuilder.buildDefaultFluxFilter(auth))
    }
}