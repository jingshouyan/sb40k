package com.github.jingshouyan.sb40k.mapper

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper
import com.github.jingshouyan.sb40k.entity.LoginRecord
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class LoginRecordMapperTest @Autowired constructor(
    val loginRecordMapper: LoginRecordMapper
) {

    @Test
    fun findByToken() {
        val record = loginRecordMapper.selectOne(
            LambdaQueryWrapper<LoginRecord>().eq(LoginRecord::token, "testtoken")
        )
        println(record)
    }

    @Test
    @Transactional
    fun updateLogoutAtByToken() {
        val now = System.currentTimeMillis()
        loginRecordMapper.update(
            null,
            LambdaUpdateWrapper<LoginRecord>()
                .eq(LoginRecord::token, "testtoken")
                .set(LoginRecord::logoutAt, now)
        )
    }

    @Test
    @Transactional
    fun updateLogoutAtByUserId() {
        val now = System.currentTimeMillis()
        loginRecordMapper.update(
            null,
            LambdaUpdateWrapper<LoginRecord>()
                .eq(LoginRecord::userId, 1L)
                .gt(LoginRecord::logoutAt, now)
                .set(LoginRecord::logoutAt, now)
        )
    }

}
