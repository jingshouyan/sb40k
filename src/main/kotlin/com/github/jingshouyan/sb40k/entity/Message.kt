package com.github.jingshouyan.sb40k.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table
class Message(
    senderId: Long,
    deviceId: String,

    ) : AuditableEntity()