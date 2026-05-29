package com.github.jingshouyan.sb40k.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.github.jingshouyan.sb40k.entity.LoginRecord
import com.github.jingshouyan.sb40k.mapper.LoginRecordMapper
import com.github.jingshouyan.sb40k.service.LoginRecordService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginRecordServiceImpl(
    private val loginRecordMapper: LoginRecordMapper
) : LoginRecordService {

    @Transactional
    override fun addLoginRecord(loginRecord: LoginRecord) {
        val now = System.currentTimeMillis()
        loginRecordMapper.update(
            null,
            KtUpdateWrapper(LoginRecord::class.java)
                .eq(LoginRecord::userId, loginRecord.userId)
                .eq(LoginRecord::deviceId, loginRecord.deviceId)
                .gt(LoginRecord::logoutAt, now)
                .set(LoginRecord::logoutAt, now)
        )
        loginRecordMapper.insert(loginRecord)
    }

    override fun logoutToken(token: String) {
        val record = loginRecordMapper.selectOne(
            KtQueryWrapper(LoginRecord::class.java).eq(LoginRecord::token, token)
        )
        if (record != null) {
            loginRecordMapper.update(
                null,
                KtUpdateWrapper(LoginRecord::class.java)
                    .eq(LoginRecord::token, token)
                    .set(LoginRecord::logoutAt, System.currentTimeMillis())
            )
        }
    }

    @Transactional
    override fun logoutUser(userId: String) {
        val now = System.currentTimeMillis()
        loginRecordMapper.update(
            null,
            KtUpdateWrapper(LoginRecord::class.java)
                .eq(LoginRecord::userId, userId)
                .gt(LoginRecord::logoutAt, now)
                .set(LoginRecord::logoutAt, now)
        )
    }
}
