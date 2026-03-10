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
        user = userRepository.save(user)
        assertEquals(user.version, 0L)
        println("---------------------------")
        user.password = "newpassword"
        user = userRepository.save(user)
        assertEquals(user.version, 1L)
        println("---------------------------")
        var ou = userRepository.findByUsername("testuser")
        assertEquals(ou.isPresent, true)
        userRepository.delete(ou.get())
    }


}