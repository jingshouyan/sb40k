package com.github.jingshouyan.sb40k.repository

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LoginRecordRepositoryTest @Autowired constructor(val loginRecordRepository: LoginRecordRepository) {

    @Test
    fun findByToken() {
        val record = loginRecordRepository.findByToken("testtoken")
        println(record)
    }

    @Test
    @Transactional
    fun updateLogoutAtByToken() {
        val now = System.currentTimeMillis()
        loginRecordRepository.updateLogoutAtByToken(now, "testtoken")
    }

    @Test
    @Transactional
    fun updateLogoutAtByUserId() {
        val now = System.currentTimeMillis()
        loginRecordRepository.updateLogoutAtByUserId(now, 1L)
    }

}