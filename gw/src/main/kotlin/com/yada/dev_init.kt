package com.yada

import com.fasterxml.jackson.databind.ObjectMapper
import com.yada.services.IAppService
import com.yada.services.IOrgService
import com.yada.services.ISvcService
import com.yada.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import com.fasterxml.jackson.module.kotlin.*
import com.yada.model.App
import com.yada.model.Org
import com.yada.model.Svc
import com.yada.model.User

private val orgJson = """

""".trimIndent()

private val userJson = """

""".trimIndent()

private val appJson = """
    
""".trimIndent()

private val svcJson = """
    
""".trimIndent()

@Profile("dev")
@Component
class InitDevDataRunner @Autowired constructor(
        private val orgSvc: IOrgService,
        private val usrSvc: IUserService,
        private val appSvc: IAppService,
        private val svcSvc: ISvcService
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val orgs = jacksonObjectMapper().readValue<List<Org>>(orgJson)
        val usrs = jacksonObjectMapper().readValue<List<User>>(userJson)
        val apps = jacksonObjectMapper().readValue<List<App>>(appJson)
        val svcs = jacksonObjectMapper().readValue<List<Svc>>(svcJson)

        orgs.forEach { orgSvc.createOrUpdate(it) }
        usrs.forEach { usrSvc.createOrUpdate(it) }
        apps.forEach { appSvc.createOrUpdate(it) }
        svcs.forEach { svcSvc.createOrUpdate(it) }
    }
}