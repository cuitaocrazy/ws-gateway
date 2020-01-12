package com.yada.controllers

import com.yada.model.*
import com.yada.repository.AppRepository
import com.yada.services.IAppService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/app")
class AppController @Autowired constructor(private val repo: AppRepository, private val app: IAppService) {
    @GetMapping
    fun get(): Mono<List<String>> = app.getAllIds().collectList()

    @GetMapping("save")
    fun save() = repo.saveAll(listOf(
            App(
                    "app1",
                    setOf(
                            SvcRes(
                                    "svc1",
                                    setOf(
                                            Res(
                                                    "/api/test1",
                                                    setOf(
                                                            Operator.CREATE,
                                                            Operator.READ,
                                                            Operator.UPDATE,
                                                            Operator.DELETE
                                                    )
                                            )
                                    )
                            ),
                            SvcRes(
                                    "svc1",
                                    setOf(
                                            Res(
                                                    "/api/test2",
                                                    setOf(
                                                            Operator.CREATE,
                                                            Operator.READ,
                                                            Operator.UPDATE,
                                                            Operator.DELETE
                                                    )
                                            )
                                    )
                            ),
                            SvcRes(
                                    "svc2",
                                    setOf(
                                            Res(
                                                    "/api/test3",
                                                    setOf(
                                                            Operator.CREATE,
                                                            Operator.READ,
                                                            Operator.UPDATE
                                                    )
                                            )
                                    )
                            )
                    ),
                    setOf(
                            Role(
                                    "role1",
                                    setOf(
                                            SvcRes(
                                                    "svc1",
                                                    setOf(
                                                            Res(
                                                                    "/api/test1",
                                                                    setOf(
                                                                            Operator.CREATE,
                                                                            Operator.READ
                                                                    )
                                                            )
                                                    )
                                            ),
                                            SvcRes(
                                                    "svc2",
                                                    setOf(
                                                            Res(
                                                                    "/api/test3",
                                                                    setOf(
                                                                            Operator.CREATE,
                                                                            Operator.READ
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            ),
            App(
                    "app2",
                    setOf(
                            SvcRes(
                                    "svc1",
                                    setOf(
                                            Res(
                                                    "/api/test1",
                                                    setOf(
                                                            Operator.CREATE,
                                                            Operator.READ,
                                                            Operator.UPDATE
                                                    )
                                            )
                                    )
                            )
                    ),
                    setOf(
                            Role(
                                    "role1",
                                    setOf(
                                            SvcRes(
                                                    "svc1",
                                                    setOf(
                                                            Res(
                                                                    "/api/test1",
                                                                    setOf(
                                                                            Operator.CREATE,
                                                                            Operator.READ
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            )
    ))
}