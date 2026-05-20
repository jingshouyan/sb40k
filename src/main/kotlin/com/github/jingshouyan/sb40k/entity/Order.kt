package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.annotation.Version
import java.time.LocalDateTime

@TableName("t_order")
class Order(

    var userId: Long,

    var totalAmount: Int,

    var status: OrderStatus = OrderStatus.CREATED,

    var paymentStatus: PaymentStatus = PaymentStatus.UNPAID,

    var paidAt: LocalDateTime? = null,
    var shippedAt: LocalDateTime? = null,

    @Version
    var version: Long? = null,
) : LongIdEntity() {

    @TableField(exist = false)
    var user: User? = null

    @TableField(exist = false)
    var items: MutableList<OrderItem> = mutableListOf()

    fun addItem(item: OrderItem) {
        item.orderId = this.id ?: 0L
        items += item
    }

    fun calculateTotal() {
        totalAmount = items.sumOf { it.price * it.quantity }
    }
}
