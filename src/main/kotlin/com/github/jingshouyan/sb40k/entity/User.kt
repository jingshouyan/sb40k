package com.github.jingshouyan.sb40k.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table
class User(

    @Column(nullable = false, unique = true, length = 50, updatable = false)
    var username: String,

    @Column(length = 50)
    var password: String,

    @Column(nullable = false, length = 100)
    var email: String,

    @Version
    var version: Long? = null, // 乐观锁：你前几天问的就是这个

) : AuditableEntity()
