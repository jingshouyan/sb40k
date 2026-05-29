package com.github.jingshouyan.sb40k.util

import kotlin.reflect.KProperty1

/**
 * 驼峰属性名 → 数据库下划线列名并加 `c_` 前缀。
 *
 * 与 MyBatis Plus 的 `column-format: c_%s` + `map-underscore-to-camel-case` 等价。
 * MyBatis Plus 内部的 [com.baomidou.mybatisplus.core.metadata.TableInfo] 也有此映射，
 * 但 [com.baomidou.mybatisplus.core.toolkit.TableInfoHelper] 需 Spring 运行时才能初始化，
 * 编译期/静态上下文中此工具函数直接使用相同的转换规则。
 *
 *  propCol(User::email)          → "c_email"
 *  propCol(User::createdAt)      → "c_created_at"
 *  propCol(VerificationCode::sentAt) → "c_sent_at"
 */
fun <T> propCol(property: KProperty1<T, *>): String = "c_${camelToSnake(property.name)}"

/**
 * 驼峰 → 下划线
 * camelToSnake("idType")    → "id_type"
 * camelToSnake("createdAt") → "created_at"
 */
fun camelToSnake(name: String): String =
    name.replace(Regex("([a-z])([A-Z])")) { "${it.groupValues[1]}_${it.groupValues[2].lowercase()}" }
