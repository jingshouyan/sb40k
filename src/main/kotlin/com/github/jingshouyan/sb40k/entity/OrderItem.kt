package com.github.jingshouyan.sb40k.entity

import jakarta.persistence.*

@Entity
@Table
class OrderItem(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null,

    // 商品 ID（业务 ID，不用外键强耦合）
    @Column(nullable = false)
    var productId: Long,

    @Column(nullable = false)
    var productName: String,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var quantity: Int
) : AuditableEntity()
