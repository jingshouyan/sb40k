package com.github.jingshouyan.sb40k.service

import com.github.jingshouyan.sb40k.entity.User

interface UserService {

    fun addUser(user: User): User

    fun checkPassword(idType: Int, id: String, password: String): User
}