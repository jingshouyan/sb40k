package com.github.jingshouyan.sb40k.service.impl

import com.github.jingshouyan.sb40k.entity.LoginRecord
import com.github.jingshouyan.sb40k.repository.LoginRecordRepository
import com.github.jingshouyan.sb40k.service.LoginRecordService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class LoginRecordServiceImpl(val loginRecordRepository: LoginRecordRepository) : LoginRecordService {

    @Transactional
    override fun addLoginRecord(loginRecord: LoginRecord) {
        val now = System.currentTimeMillis()
        loginRecordRepository.updateLogoutAtByUserId(now, loginRecord.userId, loginRecord.deviceId)
        loginRecordRepository.save(loginRecord)
    }

    override fun logoutToken(token: String) {
        loginRecordRepository.findByToken(token).ifPresent {
            it.logoutAt = System.currentTimeMillis()
            loginRecordRepository.save(it)
        }
    }

    @Transactional
    override fun logoutUser(userId: Long) {
        val now = System.currentTimeMillis()

        loginRecordRepository.updateLogoutAtByUserId(now, userId)
    }
}