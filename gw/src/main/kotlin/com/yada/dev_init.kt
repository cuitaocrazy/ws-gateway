package com.yada

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.reactivestreams.client.MongoClient
import com.yada.web.model.Org
import com.yada.web.model.Role
import com.yada.web.model.Svc
import com.yada.web.model.User
import com.yada.web.services.IOrgService
import com.yada.web.services.IRoleService
import com.yada.web.services.ISvcService
import com.yada.web.services.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val orgJson = """
[
  {
    "id": "00",
    "name": "总机构"
  },
  {
    "id": "001",
    "name": "机构一"
  },
  {
    "id": "001001",
    "name": "机构一1"
  },
  {
    "id": "002",
    "name": "机构二"
  },
  {
    "id": "003",
    "name": "机构三"
  },
  {
    "id": "004",
    "name": "机构四"
  },
  {
    "id": "005",
    "name": "机构五"
  }
]
""".trimIndent()

private val userJson = """
[
  {
    "id": "admin",
    "orgId": "00",
    "roles": [
      "role1"
    ]
  }
]
""".trimIndent()

private val roleJson = """
[
  {
    "id": "role1",
    "svcs": [
      {
        "id": "service-1",
        "resources": [
          {
            "uri": "/res1",
            "ops": [
              "READ",
              "CREATE",
              "UPDATE",
              "DELETE"
            ]
          },
          {
            "uri": "/terminal",
            "ops": [
                "READ"
            ]
          }
        ]
      },
      {
        "id": "service-2",
        "resources": [
          {
            "uri": "/trans",
            "ops": [
              "READ"
            ]
          }
        ]
      }
    ]
  },
  {
    "id": "role2",
    "svcs": [
      {
        "id": "service-1",
        "resources": [
          {
            "uri": "/merchant",
            "ops": [
              "READ",
              "CREATE",
              "UPDATE",
              "DELETE"
            ]
          }
        ]
      }
    ]
  }
]
""".trimIndent()

private val svcJson = """
[
  {
    "id": "service-1",
    "resources": [
      {
        "uri": "/res1",
        "ops": [
          "READ",
          "CREATE",
          "UPDATE",
          "DELETE"
        ]
      },
      {
        "uri": "/terminal",
        "ops": [
          "READ",
          "CREATE",
          "UPDATE",
          "DELETE"
        ]
      },
      {
        "uri": "/trans",
        "ops": [
          "READ"
        ]
      }
    ]
  },
  {
    "id": "service-2",
    "resources": [
      {
        "uri": "/merchant",
        "ops": [
          "READ"
        ]
      },
      {
        "uri": "/terminal",
        "ops": [
          "READ"
        ]
      },
      {
        "uri": "/trans",
        "ops": [
          "READ"
        ]
      }
    ]
  },
  {
    "id": "service-3",
    "resources": [
      {
        "uri": "/merchant",
        "ops": [
          "CREATE"
        ]
      },
      {
        "uri": "/terminal",
        "ops": [
          "CREATE"
        ]
      },
      {
        "uri": "/trans",
        "ops": [
          "READ"
        ]
      }
    ]
  },
  {
    "id": "service-4",
    "resources": [
      {
        "uri": "/merchant",
        "ops": [
          "UPDATE"
        ]
      },
      {
        "uri": "/terminal",
        "ops": [
          "UPDATE"
        ]
      },
      {
        "uri": "/trans",
        "ops": [
          "READ"
        ]
      }
    ]
  }
]
""".trimIndent()

@Profile("dev")
@Component
open class InitDevDataRunner @Autowired constructor(
        private val orgSvc: IOrgService,
        private val usrSvc: IUserService,
        private val roleSvc: IRoleService,
        private val svcSvc: ISvcService,
        private val client: MongoClient,
        @Value("\${yada.db.mongo.db:yada_auth}")
        private val dbName: String
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {

        val orgs = jacksonObjectMapper().readValue<List<Org>>(orgJson)
        val svcs = jacksonObjectMapper().readValue<List<Svc>>(svcJson)
        val apps = jacksonObjectMapper().readValue<List<Role>>(roleJson)
        val usrs = jacksonObjectMapper().readValue<List<User>>(userJson)

        Mono.from(client.getDatabase(dbName).drop())
                .thenMany(Flux.mergeSequential(orgs.map { orgSvc.createOrUpdate(it) }))
                .thenMany(Flux.mergeSequential(svcs.map { svcSvc.createOrUpdate(it) }))
                .thenMany(Flux.mergeSequential(apps.map { roleSvc.createOrUpdate(it) }))
                .thenMany(Flux.mergeSequential(usrs.map { usrSvc.createOrUpdate(it) }))
                .subscribe()
    }
}