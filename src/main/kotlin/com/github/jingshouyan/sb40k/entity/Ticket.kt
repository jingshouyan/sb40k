package com.github.jingshouyan.sb40k.entity

class Ticket(
    val userId: Long,
    val deviceType: Byte,
    val ts: Long,
    val deviceId: String,
) {
    override fun toString(): String {
        return "Ticket(userId=$userId, deviceType=$deviceType, ts=$ts, deviceId='$deviceId')"
    }
}