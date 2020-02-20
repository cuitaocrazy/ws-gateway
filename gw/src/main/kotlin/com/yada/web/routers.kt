package com.yada.web

import com.yada.filters.AuthApiHandlerFilter
import com.yada.filters.AuthHandlerFilter
import com.yada.web.handlers.AdminAuthHandler
import com.yada.web.handlers.AuthHandler
import com.yada.web.handlers.apis.AppHandler
import com.yada.web.handlers.apis.OrgHandler
import com.yada.web.handlers.apis.SvcHandler
import com.yada.web.handlers.apis.UserHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
open class AuthRouterConfig @Autowired constructor(private val authHandler: AuthHandler, private val authFilter: AuthHandlerFilter, private val authApiFilter: AuthApiHandlerFilter) {
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
            GET("/logout", authHandler::logout)
            filter(authFilter::filter)
        }

        "".nest {
            POST("/change_pwd", authHandler::changePwd)
            GET("/refresh_token", authHandler::refreshToken)
            filter(authApiFilter::filter)
        }
    }
}

@Configuration
open class AdminAuthRouterConfig @Autowired constructor(private val adminAuthHandler: AdminAuthHandler, private val authApiFilter: AuthApiHandlerFilter) {
    @Bean
    open fun adminAuthRouter() = router {
        "/admin".nest {
            GET("", adminAuthHandler::index)
            POST("/login", adminAuthHandler::login)
        }

        "/admin".nest {
            POST("/logout", adminAuthHandler::logout)
            POST("/apis/change_pwd", adminAuthHandler::changePwd)
            GET("/apis/refresh_token", adminAuthHandler::refreshToken)
            filter(authApiFilter::filter)
        }
    }
}

@Configuration
open class AdminApiRouterConfig @Autowired constructor(
        private val appHandler: AppHandler,
        private val orgHandler: OrgHandler,
        private val svcHandler: SvcHandler,
        private val userHandler: UserHandler,
        private val authApiFilter: AuthApiHandlerFilter) {
    @Bean
    open fun adminApiRouter() = router {
        "/admin/apis".nest {
            "/app".nest {
                GET("", appHandler::getAll)
                GET("/{id}", appHandler::get)
                GET("/{id}/exist", appHandler::exist)
                PUT("", appHandler::createOrUpdate)
                DELETE("/{id}", appHandler::delete)
            }
            "/org".nest {
                GET("", orgHandler::getTree)
                GET("/{id}", orgHandler::get)
                GET("/{id}/exist", orgHandler::exist)
                PUT("", orgHandler::createOrUpdate)
                DELETE("/{id}", orgHandler::delete)
            }
            "/svc".nest {
                GET("", svcHandler::getAll)
                GET("/{id}", svcHandler::get)
                PUT("", svcHandler::createOrUpdate)
                DELETE("", svcHandler::delete)
            }
            "/user".nest {
                GET("", userHandler::getUsersBy)
                GET("/{id}", userHandler::get)
                GET("/{id}/exist", userHandler::exist)
                PUT("", userHandler::createOrUpdate)
                DELETE("/{id}", userHandler::delete)
            }
        }
        filter(authApiFilter::filter)
    }
}