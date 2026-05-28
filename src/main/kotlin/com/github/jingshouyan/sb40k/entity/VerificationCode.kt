package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.TableName

@TableName("t_verification_code")
class VerificationCode(

    var userId: String? = null,

    var target: String,

    var code: String,

    var businessType: String,

    var expireAt: Long,

    var sentAt: Long,

) : StringIdEntity() {
    override fun idPrefix() = EntityPrefix.VERIFICATION_CODE
}
