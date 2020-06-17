package com.yada.web.routers

import com.yada.adminPath
import com.yada.sc2.web.FilterContextBuilder
import com.yada.web.filters.WhitelistHandlerFilter
import com.yada.web.handlers.AdminAuthHandler
import com.yada.web.security.AdminAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
open class AdminAuthRouterConfig @Autowired constructor(
        private val adminAuthHandler: AdminAuthHandler,
        private val whitelistFilter: WhitelistHandlerFilter,
        private val auth: AdminAuth
) {
    @Bean
    open fun adminAuthRouter() = router {
        adminPath.nest {
            GET("", adminAuthHandler::index)
            POST("/login", adminAuthHandler::login)
            filter(whitelistFilter)
            filter(FilterContextBuilder.buildDefaultFluxFilter(auth))
        }

        adminPath.nest {
            POST("/apis/logout", adminAuthHandler::logout)
            POST("/apis/change_pwd", adminAuthHandler::changePwd)
            GET("/apis/refresh_token", adminAuthHandler::refreshToken)
            filter(whitelistFilter)
//            filter(webFluxAdminAuthFilter)
//            filter(authAdminApiFilter)
        }
    }
}