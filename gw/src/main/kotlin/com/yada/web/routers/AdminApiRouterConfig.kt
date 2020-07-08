package com.yada.web.routers

import com.yada.adminPath
import com.yada.security.web.FilterContextBuilder
import com.yada.web.filters.AuthApiHandlerFilter
import com.yada.web.filters.WhitelistHandlerFilter
import com.yada.web.handlers.apis.*
import com.yada.web.security.AdminAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
open class AdminApiRouterConfig @Autowired constructor(
        private val roleHandler: RoleHandler,
        private val orgHandler: OrgHandler,
        private val svcHandler: SvcHandler,
        private val userHandler: UserHandler,
        private val whitelistFilter: WhitelistHandlerFilter,
        private val authApiHandlerFilter: AuthApiHandlerFilter,
        private val defaultRoleSvcResHandler: DefaultRoleSvcResHandler,
        private val auth: AdminAuth
) {
    @Bean
    open fun adminApiRouter() = router {
        """${adminPath}/apis""".nest {
            "/role".nest {
                GET("", roleHandler::getAll)
                GET("/{id}", roleHandler::get)
                GET("/{id}/exist", roleHandler::exist)
                PUT("", roleHandler::createOrUpdate)
                DELETE("/{id}", roleHandler::delete)
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
                GET("/actual_svc_ids", svcHandler::actualSvcIds)
                GET("/{id}", svcHandler::get)
                PUT("", svcHandler::createOrUpdate)
                DELETE("/{id}", svcHandler::delete)
                GET("/{id}/actual_res", svcHandler::actualRes)
            }
            "/user".nest {
                GET("", userHandler::getUsersBy)
                GET("/{id}", userHandler::get)
                GET("/{id}/exist", userHandler::exist)
                PUT("/{id}/reset_pwd", userHandler::resetPwd)
                PUT("", userHandler::createOrUpdate)
                DELETE("/{id}", userHandler::delete)
            }
            "/default_role".nest {
                GET("", defaultRoleSvcResHandler::get)
                PUT("", defaultRoleSvcResHandler::createOrUpdate)
            }
            filter(whitelistFilter)
            filter(authApiHandlerFilter)
        }

        filter(FilterContextBuilder.buildDefaultFluxFilter(auth))
    }
}