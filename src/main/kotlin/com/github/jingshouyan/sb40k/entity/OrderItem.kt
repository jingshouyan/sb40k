package com.github.jingshouyan.sb40k.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName

@TableName("t_order_item")
class OrderItem(

    var orderId: Long? = null,

    var productId: Long,

    var productName: String,

    var price: Int,

    var quantity: Int
) : LongIdEntity() {

    @TableField(exist = false)
    var order: Order? = null
}
