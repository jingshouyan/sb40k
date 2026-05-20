package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId

enum class OrderStatus {
    CREATED,        // 已创建
    PAID,           // 已支付
    SHIPPED,        // 已发货
    FINISHED,       // 完成
    CANCELED        // 取消
}

enum class PaymentStatus {
    UNPAID,
    PAID,
    REFUNDED
}

abstract class BaseIdEntity(
    @TableId(type = IdType.AUTO)
    val id: Long? = null
)


abstract class AuditableEntity(
    var deletedAt: Long? = null
) : BaseIdEntity() {

    @TableField(fill = FieldFill.INSERT)
    var createBy: String? = null

    @TableField(fill = FieldFill.INSERT)
    var createdAt: Long? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var updatedBy: String? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var updatedAt: Long? = null
}
