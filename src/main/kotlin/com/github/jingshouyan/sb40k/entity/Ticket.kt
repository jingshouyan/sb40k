package com.github.jingshouyan.sb40k.entity

class Ticket(
    val token: String,
    val deviceType: Int,
    val deviceId: String,
    val expiredAt: Long,
    val user: User,
)