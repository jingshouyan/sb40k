package com.github.jingshouyan.sb40k.entity

class Ticket(
    val userId: String,
    val deviceType: Byte,
    val ts: Long,
    val deviceId: String,
    var token: String,
) {
    override fun toString(): String {
        return """{"userId": $userId,"deviceType": $deviceType,"ts": $ts,"deviceId": "$deviceId,"token": "$token"}"""
    }
}