package com.github.jingshouyan.sb40k.socketio.cache

import java.util.*

data class ConnectionInfo(
    val sessionId: UUID,
    val remoteAddress: String,
    val token: String,
    val userId: String,
    val deviceType: Byte,
    val deviceId: String,
) {

    override fun toString(): String {
        return """{"sessionId":"$sessionId","remoteAddress":"$remoteAddress","token":"$token","userId":"$userId","deviceType":$deviceType,"deviceId":"$deviceId"}"""
    }
}
