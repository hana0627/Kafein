package com.hana.baseproject.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    fun index(): String {
        return "안녕!!! + index"
    }
}