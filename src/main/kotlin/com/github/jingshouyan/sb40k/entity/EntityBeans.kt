package com.github.jingshouyan.sb40k.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

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

@MappedSuperclass
abstract class BaseIdEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AuditableEntity(
    var deletedAt: Long? = null
) : BaseIdEntity() {

    @CreatedBy
    var createBy: String? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Long? = null

    @LastModifiedBy
    var updatedBy: String? = null

    @LastModifiedDate
    var updatedAt: Long? = null
}


