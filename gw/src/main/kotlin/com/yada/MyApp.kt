package com.yada

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class MyApp

fun main(args: Array<String>) {
    runApplication<MyApp>(*args)
}