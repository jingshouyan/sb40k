package com.github.jingshouyan.sb40k.socketio.cache

data class ConnectionInfo(
    val sessionId: String,
    val remoteAddress: String,
    val authToken: String,
    val userId: Long,
    val deviceType: Int,
    val deviceId: String
)
