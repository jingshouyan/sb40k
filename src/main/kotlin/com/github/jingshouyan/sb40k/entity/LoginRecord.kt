package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.TableName

@TableName("t_login_record")
class LoginRecord(
    val userId: String,

    val token: String,
    val deviceType: Byte,
    val deviceId: String,
    val clientIp: String,
    val remoteIp: String,
    val deviceName: String,
    val deviceDesc: String,
    val loginAt: Long,
    val result: Int,
    val extInfo: String,
) : LongIdEntity() {
    var logoutAt: Long = Long.MAX_VALUE
}
