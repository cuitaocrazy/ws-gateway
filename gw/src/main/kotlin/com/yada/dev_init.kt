package com.yada

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.reactivestreams.client.MongoClient
import com.yada.model.App
import com.yada.model.Org
import com.yada.model.Svc
import com.yada.model.User
import com.yada.services.IAppService
import com.yada.services.IOrgService
import com.yada.services.ISvcService
import com.yada.services.IUserService
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
      {
        "appId": "app-1",
        "roleName": "admin"
      },
      {
        "appId": "app-1",
        "roleName": "user"
      },
      {
        "appId": "app-2",
        "roleName": "admin"
      }
    ]
  }
]
""".trimIndent()

private val appJson = """
[
  {
    "id": "app-1",
    "resources": [
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
      },
      {
        "id": "service-1",
        "resources": [
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
    ],
    "roles": [
      {
        "name": "admin",
        "resources": [
          {
            "id": "service-1",
            "resources": [
              {
                "uri": "/merchant",
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
        "name": "user",
        "resources": [
          {
            "id": "service-1",
            "resources": [
              {
                "uri": "/merchant",
                "ops": [
                  "READ"
                ]
              }
            ]
          }
        ]
      },
      {
        "name": "anon",
        "resources": [
          {
            "id": "service-1",
            "resources": [
              {
                "uri": "/merchant",
                "ops": [
                  "READ"
                ]
              }
            ]
          }
        ]
      }
    ]
  },
  {
    "id": "app-2",
    "resources": [
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
    ],
    "roles": [
      {
        "name": "admin",
        "resources": [
          {
            "id": "service-1",
            "resources": [
              {
                "uri": "/merchant",
                "ops": [
                  "READ"
                ]
              }
            ]
          }
        ]
      },
      {
        "name": "user",
        "resources": [
          {
            "id": "service-1",
            "resources": [
              {
                "uri": "/merchant",
                "ops": [
                  "READ"
                ]
              }
            ]
          }
        ]
      },
      {
        "name": "anon",
        "resources": [
          {
            "id": "service-1",
            "resources": [
              {
                "uri": "/merchant",
                "ops": [
                  "READ"
                ]
              }
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
        "uri": "/merchant",
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
        private val appSvc: IAppService,
        private val svcSvc: ISvcService,
        private val client: MongoClient,
        @Value("\${yada.db.mongo.db:yada_auth}")
        private val dbName: String
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {

        val orgs = jacksonObjectMapper().readValue<List<Org>>(orgJson)
        val svcs = jacksonObjectMapper().readValue<List<Svc>>(svcJson)
        val apps = jacksonObjectMapper().readValue<List<App>>(appJson)
        val usrs = jacksonObjectMapper().readValue<List<User>>(userJson)

        Mono.from(client.getDatabase(dbName).drop()).
                thenMany(Flux.mergeSequential(orgs.map { orgSvc.createOrUpdate(it) })).
                thenMany(Flux.mergeSequential(svcs.map { svcSvc.createOrUpdate(it) })).
                thenMany(Flux.mergeSequential(apps.map { appSvc.createOrUpdate(it) })).
                thenMany(Flux.mergeSequential(usrs.map { usrSvc.createOrUpdate(it) })).
                subscribe()

//        Mono.from(client.startSession()).flatMap { session->
//            session.startTransaction()
//            session.close()
//            client.getDatabase("").drop()
//            null
//        }

    }
}