package com.yada.controllers

import com.yada.model.App
import com.yada.services.IAppService
import com.yada.withNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/admin/apis/app")
class AppController @Autowired constructor(private val appService: IAppService) {
    @GetMapping
    fun getAll(): Flux<App> = appService.getAll()

    @PutMapping
    fun createOrUpdate(@RequestBody app: App) = appService.createOrUpdate(app)

    @GetMapping("{id}")
    fun getApp(@PathVariable("id") id: String) = withNotFound(appService.get(id))

    @GetMapping("{id}/exist")
    fun exist(@PathVariable("id") id: String) = appService.exist(id)

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: String) = appService.delete(id)

//    @GetMapping("save")
//    fun save(): Flux<App> = repo.saveAll(listOf(
//            App(
//                    "app1",
//                    setOf(
//                            SvcRes(
//                                    "svc1",
//                                    setOf(
//                                            Res(
//                                                    "/api/test1",
//                                                    setOf(
//                                                            Operator.CREATE,
//                                                            Operator.READ,
//                                                            Operator.UPDATE,
//                                                            Operator.DELETE
//                                                    )
//                                            )
//                                    )
//                            ),
//                            SvcRes(
//                                    "svc1",
//                                    setOf(
//                                            Res(
//                                                    "/api/test2",
//                                                    setOf(
//                                                            Operator.CREATE,
//                                                            Operator.READ,
//                                                            Operator.UPDATE,
//                                                            Operator.DELETE
//                                                    )
//                                            )
//                                    )
//                            ),
//                            SvcRes(
//                                    "svc2",
//                                    setOf(
//                                            Res(
//                                                    "/api/test3",
//                                                    setOf(
//                                                            Operator.CREATE,
//                                                            Operator.READ,
//                                                            Operator.UPDATE
//                                                    )
//                                            )
//                                    )
//                            )
//                    ),
//                    setOf(
//                            Role(
//                                    "role1",
//                                    setOf(
//                                            SvcRes(
//                                                    "svc1",
//                                                    setOf(
//                                                            Res(
//                                                                    "/api/test1",
//                                                                    setOf(
//                                                                            Operator.CREATE,
//                                                                            Operator.READ
//                                                                    )
//                                                            )
//                                                    )
//                                            ),
//                                            SvcRes(
//                                                    "svc2",
//                                                    setOf(
//                                                            Res(
//                                                                    "/api/test3",
//                                                                    setOf(
//                                                                            Operator.CREATE,
//                                                                            Operator.READ
//                                                                    )
//                                                            )
//                                                    )
//                                            )
//                                    )
//                            )
//                    )
//            ),
//            App(
//                    "app2",
//                    setOf(
//                            SvcRes(
//                                    "svc1",
//                                    setOf(
//                                            Res(
//                                                    "/api/test1",
//                                                    setOf(
//                                                            Operator.CREATE,
//                                                            Operator.READ,
//                                                            Operator.UPDATE
//                                                    )
//                                            )
//                                    )
//                            )
//                    ),
//                    setOf(
//                            Role(
//                                    "role1",
//                                    setOf(
//                                            SvcRes(
//                                                    "svc1",
//                                                    setOf(
//                                                            Res(
//                                                                    "/api/test1",
//                                                                    setOf(
//                                                                            Operator.CREATE,
//                                                                            Operator.READ
//                                                                    )
//                                                            )
//                                                    )
//                                            )
//                                    )
//                            )
//                    )
//            )
//    ))
}