package com.yada

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.tools.agent.ReactorDebugAgent


@SpringBootApplication
open class MyApp

fun main(args: Array<String>) {
    ReactorDebugAgent.init()
    runApplication<MyApp>(*args)
}