package com.github.jingshouyan.sb40k.repository

import com.github.jingshouyan.sb40k.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class UserRepositoryTest @Autowired constructor(val userRepository: UserRepository) {

    @Test
    fun `save user`() {
        var user = User(
            username = "testuser",
            password = "password123",
            email = "abc@111.com"
        )
        user = userRepository.saveAndFlush(user)
        assertEquals(user.version, 0L)
        user.password = "newpassword"
        user = userRepository.saveAndFlush(user)
        assertEquals(user.version, 1L)

    }


}