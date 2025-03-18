package com.hana.baseproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BaseProjectApplication

fun main(args: Array<String>) {
    runApplication<BaseProjectApplication>(*args)
}
