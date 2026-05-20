package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.annotation.Version
import com.fasterxml.jackson.annotation.JsonIgnore

@TableName("t_user")
class User(

    var username: String,

    @JsonIgnore
    var password: String,

    var email: String? = null,

    @Version
    var version: Long? = null,

    var unlockedAt: Long = 0L,

    var tryCount: Long = 0L,

    var firstTryAt: Long = 0L,

    ) : AuditableEntity()
