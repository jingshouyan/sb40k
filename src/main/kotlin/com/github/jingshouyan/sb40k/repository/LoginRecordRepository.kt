package com.github.jingshouyan.sb40k.repository

import com.github.jingshouyan.sb40k.entity.LoginRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface LoginRecordRepository : JpaRepository<LoginRecord, Long> {

    fun findByToken(token: String): Optional<LoginRecord>

    fun findByUserIdAndLogoutAtGreaterThan(userId: Long, logoutAt: Long): List<LoginRecord>

    @Modifying
    @Query("update LoginRecord set logoutAt = :logoutAt where token = :token")
    fun updateLogoutAtByToken(logoutAt: Long, token: String): Int

    @Modifying
    @Query("update LoginRecord set logoutAt = :logoutAt where userId = :userId and logoutAt > :logoutAt")
    fun updateLogoutAtByUserId(logoutAt: Long, userId: Long): Int

    @Modifying
    @Query("update LoginRecord set logoutAt = :logoutAt where userId = :userId and logoutAt > :logoutAt and deviceId = :deviceId")
    fun updateLogoutAtByUserId(logoutAt: Long, userId: Long, deviceId: String): Int

}