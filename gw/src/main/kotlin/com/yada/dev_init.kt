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

        orgs.forEach { orgSvc.createOrUpdate(it).subscribe() }
        usrs.forEach { usrSvc.createOrUpdate(it).subscribe() }
        apps.forEach { appSvc.createOrUpdate(it).subscribe() }
        svcs.forEach { svcSvc.createOrUpdate(it).subscribe() }
    }
}