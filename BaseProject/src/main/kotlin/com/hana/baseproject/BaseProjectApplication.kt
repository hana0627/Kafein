package com.hana.baseproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BaseProjectApplication

fun main(args: Array<String>) {
    //TODO 모든필드 deleltedDate -> deletedAt으로 네이밍변경
    //TODO createdAt 필드 추가
    runApplication<BaseProjectApplication>(*args)
}
