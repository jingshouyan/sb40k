package com.github.jingshouyan.sb40k

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("com.github.jingshouyan.sb40k.mapper")
class Sb40kApplication

fun main(args: Array<String>) {
    runApplication<Sb40kApplication>(*args)
}
