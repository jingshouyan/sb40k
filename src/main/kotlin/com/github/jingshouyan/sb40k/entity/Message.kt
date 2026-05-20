package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.TableName

@TableName("t_message")
class Message(
    var senderId: Long,
    var deviceId: String,

    ) : AuditableEntity()
