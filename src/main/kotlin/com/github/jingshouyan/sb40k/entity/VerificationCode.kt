package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName
import com.github.jingshouyan.sb40k.handler.JsonMapTypeHandler

@TableName("t_verification_code")
class VerificationCode(

    var userId: String? = null,

    var account: String,

    var idType: Int,

    var code: String,

    var businessType: String,

    var lang: String? = null,

    @TableField(typeHandler = JsonMapTypeHandler::class)
    var params: Map<String, String>? = null,

    var expireAt: Long,

    var sentAt: Long,

    var verifiedAt: Long? = null,

) : StringIdEntity() {
    override fun idPrefix() = EntityPrefix.VERIFICATION_CODE
}
