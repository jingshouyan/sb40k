package com.github.jingshouyan.sb40k.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table
class User(

    @Column(nullable = false, unique = true, length = 50, updatable = false)
    var username: String,

    @JsonIgnore
    @Column(length = 100)
    var password: String,

    @Column(length = 100)
    var email: String? = null,

    @Version
    var version: Long? = null,

    var unlockedAt: Long = 0L,

    var tryCount: Long = 0L,

    var firstTryAt: Long = 0L,

    ) : AuditableEntity()
