package com.github.jingshouyan.sb40k.service

import com.github.jingshouyan.sb40k.base.R
import com.github.jingshouyan.sb40k.entity.User
import java.util.*

interface UserService {

    fun addUser(user: User): User

    fun getUser(idType: Int, id: String): Optional<User>

    fun checkPassword(user: User, password: String): R
}