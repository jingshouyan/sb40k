package com.github.jingshouyan.sb40k.repository

import com.github.jingshouyan.sb40k.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): Optional<User>
}