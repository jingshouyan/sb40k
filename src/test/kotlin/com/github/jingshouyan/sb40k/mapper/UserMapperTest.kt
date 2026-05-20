package com.github.jingshouyan.sb40k.mapper

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.github.jingshouyan.sb40k.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserMapperTest @Autowired constructor(val userMapper: UserMapper) {

    @Test
    fun `save user`() {
        var user = User(
            username = "testuser",
            password = "password123",
            email = "abc@111.com"
        )
        userMapper.insert(user)
        assertEquals(0L, user.version)
        println("---------------------------")
        user.password = "newpassword"
        userMapper.updateById(user)
        assertEquals(1L, user.version)
        println("---------------------------")
        val ou = userMapper.selectOne(
            LambdaQueryWrapper<User>().eq(User::username, "testuser")
        )
        assertEquals(true, ou != null)
        userMapper.deleteById(ou!!.id)
    }


}
