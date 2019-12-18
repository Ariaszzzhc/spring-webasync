package com.hiarias.example

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@SpringBootApplication
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args).start()
}


data class Person(@Id val id: String?, val name: String)
data class NewPerson(val name: String)


interface PersonRepository : ReactiveCrudRepository<Person, String> {
    fun findByName(name: String): Flux<Person>
    fun findFirstByName(name: String): Mono<Person>
}


@RestController
class PersonController(
    private val repository: PersonRepository
) {

    @GetMapping("/person")
    suspend fun getPersonWithName(name: String): List<Person> {
        return repository.findByName(name).asFlow().toList()
    }

    @PostMapping("/person")
    suspend fun savePerson(@RequestBody person: NewPerson): Person {
        return repository.save(Person(null, person.name)).awaitFirst()
    }
}
