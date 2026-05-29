package com.github.jingshouyan.sb40k.service.impl

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.github.jingshouyan.sb40k.entity.LoginRecord
import com.github.jingshouyan.sb40k.mapper.LoginRecordMapper
import com.github.jingshouyan.sb40k.service.LoginRecordService
import com.github.jingshouyan.sb40k.util.propCol
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
            UpdateWrapper<LoginRecord>()
                .eq(propCol(LoginRecord::userId), loginRecord.userId)
                .eq(propCol(LoginRecord::deviceId), loginRecord.deviceId)
                .gt(propCol(LoginRecord::logoutAt), now)
                .set(propCol(LoginRecord::logoutAt), now)
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
                UpdateWrapper<LoginRecord>()
                    .eq(propCol(LoginRecord::token), token)
                    .set(propCol(LoginRecord::logoutAt), System.currentTimeMillis())
            )
        }
    }

    @Transactional
    override fun logoutUser(userId: String) {
        val now = System.currentTimeMillis()
        loginRecordMapper.update(
            null,
            UpdateWrapper<LoginRecord>()
                .eq(propCol(LoginRecord::userId), userId)
                .gt(propCol(LoginRecord::logoutAt), now)
                .set(propCol(LoginRecord::logoutAt), now)
        )
    }
}
