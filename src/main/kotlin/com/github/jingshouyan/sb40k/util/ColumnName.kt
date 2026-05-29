package com.github.jingshouyan.sb40k.util

import kotlin.reflect.KProperty1

/**
 * 根据属性引用获取数据库列名（遵守 `c_%s` 命名约定）
 * 替代 [com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper] 的 Kotlin 不兼容问题
 */
fun <T> propCol(property: KProperty1<T, *>): String = "c_${property.name}"
