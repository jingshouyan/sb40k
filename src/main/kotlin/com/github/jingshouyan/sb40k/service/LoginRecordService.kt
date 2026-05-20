package com.github.jingshouyan.sb40k.service

import com.github.jingshouyan.sb40k.entity.LoginRecord

interface LoginRecordService {
    fun addLoginRecord(loginRecord: LoginRecord)
    fun logoutToken(token: String)
    fun logoutUser(userId: String)
}