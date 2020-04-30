package com.yada.web.routers

import com.yada.web.filters.AuthAdminApiHandlerFilter
import com.yada.web.filters.WhitelistHandlerFilter
import com.yada.web.handlers.apis.*
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
        private val authAdminApiFilter: AuthAdminApiHandlerFilter,
        private val whitelistFilter: WhitelistHandlerFilter,
        private val defaultRoleSvcResHandler: DefaultRoleSvcResHandler) {
    @Bean
    open fun adminApiRouter() = router {
        "/admin/apis".nest {
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
                GET("/{id}", svcHandler::get)
                PUT("", svcHandler::createOrUpdate)
                DELETE("", svcHandler::delete)
                GET("/{id}/actual_res", svcHandler::actualRes)
                GET("/actual_svc_ids", svcHandler::actualSvcIds)
            }
            "/user".nest {
                GET("", userHandler::getUsersBy)
                GET("/{id}", userHandler::get)
                GET("/{id}/exist", userHandler::exist)
                PUT("", userHandler::createOrUpdate)
                DELETE("/{id}", userHandler::delete)
            }
            "/default_role".nest {
                GET("", defaultRoleSvcResHandler::get)
                PUT("", defaultRoleSvcResHandler::createOrUpdate)
            }
        }
        filter(whitelistFilter)
        filter(authAdminApiFilter)
    }
}