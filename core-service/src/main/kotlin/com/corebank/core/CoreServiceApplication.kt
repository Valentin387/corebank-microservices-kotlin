package com.corebank.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.corebank.core", "com.corebank.commons.security"])
class CoreServiceApplication

fun main(args: Array<String>) {
    runApplication<CoreServiceApplication>(*args)
}
