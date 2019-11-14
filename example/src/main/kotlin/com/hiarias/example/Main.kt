package com.hiarias.example

import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/hello/json")
    suspend fun helloWithJson(): Hello {
        delay(100)
        return Hello("Hello")
    }

    @PostMapping("/hello/ping")
    suspend fun helloWithPostBody(@RequestBody ping: Ping): String {
        delay(100)
        return "Hello, ${ping.name}, ${ping.message}"
    }
}

data class Ping(val name: String, val message: String)

data class Hello(val message: String)
