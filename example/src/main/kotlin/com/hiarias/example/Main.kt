package com.hiarias.example

import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args).start()
}

@RestController
@RequestMapping("/example")
class HelloController {
    @GetMapping("/hello")
    suspend fun hello(): String {
        delay(100)
        return "Hello"
    }

    @GetMapping("/hello/{name}")
    suspend fun helloWithPathVariable(@PathVariable name: String): String {
        delay(100)
        return "Hello, $name"
    }

    @GetMapping("/hello/param")
    suspend fun helloWithQueryParam(name: String): String {
        delay(100)
        return "Hello, $name"
    }
}
