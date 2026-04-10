package com.github.jingshouyan.sb40k.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    indexes = [Index(
        name = "idx_user_id__logout_at",
        columnList = "userId,logoutAt"
    ), Index(name = "idx_token__logout_at", columnList = "token,logoutAt")]
)
class LoginRecord(
    @Column(updatable = false)
    val userId: Long,

    @Column(length = 64, updatable = false)
    val token: String,
    @Column(updatable = false)
    val deviceType: Byte,
    @Column(updatable = false, length = 64)
    val deviceId: String,
    @Column(updatable = false, length = 64)
    val clientIP: String,
    @Column(updatable = false, length = 64)
    val remoteIP: String,
    @Column(updatable = false, length = 32)
    val deviceName: String,
    @Column(updatable = false, length = 100)
    val deviceDesc: String,
    @Column(updatable = false)
    val loginAt: Long,
    @Column(updatable = false)
    val result: Int,
    @Column(updatable = false, length = 1024)
    val extInfo: String,
) : AuditableEntity() {
    var logoutAt: Long = Long.MAX_VALUE
}