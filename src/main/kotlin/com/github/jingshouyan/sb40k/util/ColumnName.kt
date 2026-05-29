package com.github.jingshouyan.sb40k.util

import kotlin.reflect.KProperty1

/**
 * 驼峰 → 下划线
 * camelToSnake("idType")    → "id_type"
 * camelToSnake("createdAt") → "created_at"
 */
fun camelToSnake(name: String): String =
    name.replace(Regex("([a-z])([A-Z])")) { "${it.groupValues[1]}_${it.groupValues[2].lowercase()}" }

/**
 * 根据 Kotlin 属性引用生成数据库列名（遵守 `c_%s` 下划线命名）
 * 属性驼峰 → 列名下划线 + c_ 前缀
 *
 *  propCol(User::email)      → "c_email"
 *  propCol(User::username)   → "c_username"
 *  propCol(User::createdAt)  → "c_created_at"
 *  propCol(VerificationCode::sentAt) → "c_sent_at"
 *
 * 替代 [com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper] 的 Kotlin 不兼容问题
 */
fun <T> propCol(property: KProperty1<T, *>): String = "c_${camelToSnake(property.name)}"
