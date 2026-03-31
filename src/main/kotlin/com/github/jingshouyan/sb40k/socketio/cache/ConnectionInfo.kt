package com.github.jingshouyan.sb40k.socketio.cache

import java.util.*

data class ConnectionInfo(
    val sessionId: UUID?,
    val remoteAddress: String,
    val authToken: String,
    val userId: Long,
    val deviceType: Byte,
    val deviceId: String
)
