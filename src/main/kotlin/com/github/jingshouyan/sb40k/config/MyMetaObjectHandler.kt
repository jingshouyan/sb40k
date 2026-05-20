package com.github.jingshouyan.sb40k.config

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import com.github.jingshouyan.sb40k.entity.Ticket
import org.apache.ibatis.reflection.MetaObject
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class MyMetaObjectHandler : MetaObjectHandler {

    override fun insertFill(metaObject: MetaObject) {
        val userId = getCurrentUserId()
        val now = System.currentTimeMillis()
        this.strictInsertFill(metaObject, "createBy", String::class.java, userId)
        this.strictInsertFill(metaObject, "createdAt", Long::class.java, now)
        this.strictInsertFill(metaObject, "updatedBy", String::class.java, userId)
        this.strictInsertFill(metaObject, "updatedAt", Long::class.java, now)
    }

    override fun updateFill(metaObject: MetaObject) {
        val userId = getCurrentUserId()
        val now = System.currentTimeMillis()
        this.strictUpdateFill(metaObject, "updatedBy", String::class.java, userId)
        this.strictUpdateFill(metaObject, "updatedAt", Long::class.java, now)
    }

    private fun getCurrentUserId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val ticket = authentication?.principal as? Ticket ?: return "system"
        return ticket.userId.toString()
    }
}
