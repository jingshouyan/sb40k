package com.github.jingshouyan.sb40k

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class Sb40kApplication

fun main(args: Array<String>) {
    runApplication<Sb40kApplication>(*args)
}
