package com.github.jingshouyan.sb40k.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table
class Order(

    // 下单用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    // 总金额
    @Column(nullable = false)
    var totalAmount: Int,

    // 状态（待支付、已支付、已发货…）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.CREATED,

    // 支付状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var paymentStatus: PaymentStatus = PaymentStatus.UNPAID,

    var paidAt: LocalDateTime? = null,
    var shippedAt: LocalDateTime? = null,

    @Version
    var version: Long? = null,   // 乐观锁
) : AuditableEntity() {
    // 一对多：OrderItem 列表
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItem> = mutableListOf()

    fun addItem(item: OrderItem) {
        item.order = this
        items += item
    }

    fun calculateTotal() {
        totalAmount = items.sumOf { it.price * it.quantity }
    }
}
