package com.github.jingshouyan.sb40k.config

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor
import com.github.jingshouyan.sb40k.entity.StringIdEntity
import com.github.jingshouyan.sb40k.util.Base58
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicLong

@Configuration
@MapperScan("com.github.jingshouyan.sb40k.mapper")
class MybatisPlusConfig {

    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor {
        val interceptor = MybatisPlusInterceptor()
        interceptor.addInnerInterceptor(OptimisticLockerInnerInterceptor())
        return interceptor
    }

    @Bean
    fun identifierGenerator(@Value($$"${biz.snowflake-worker-id:0}") workerId: Long): IdentifierGenerator {
        return SnowflakeIdGenerator(workerId)
    }
}

/**
 * Snowflake ID generator
 * Bit layout: 1 sign + 45 timestamp + 6 worker + 12 sequence
 */
class SnowflakeIdGenerator(
    private val workerId: Long
) : IdentifierGenerator {

    companion object {
        private const val EPOCH = 1577836800000L // 2020-01-01 00:00:00 UTC
        private const val WORKER_BITS = 6L
        private const val SEQUENCE_BITS = 12L
        private const val MAX_WORKER_ID = (1L shl WORKER_BITS.toInt()) - 1  // 63
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS.toInt()) - 1  // 4095
        private const val TIMESTAMP_SHIFT = WORKER_BITS + SEQUENCE_BITS  // 22
        private const val WORKER_SHIFT = SEQUENCE_BITS                   // 16
    }

    init {
        require(workerId in 0..MAX_WORKER_ID) {
            "workerId must be 0..$MAX_WORKER_ID, got $workerId"
        }
    }

    private var lastTimestamp = -1L
    private val sequence = AtomicLong(0L)

    override fun nextId(entity: Any?): Long {
        return nextId()
    }

    override fun nextUUID(entity: Any?): String? {
        val id = nextId()
        val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)
        buffer.putLong(id)
        val stringId = Base58.encode(buffer.array())
        if (entity is StringIdEntity)
            return entity.idPrefix() + stringId
        return stringId
    }

    @Synchronized
    fun nextId(): Long {
        var timestamp = currentTimeMillis()

        if (timestamp < lastTimestamp) {
            throw IllegalStateException("Clock moved backwards, refusing to generate id for ${lastTimestamp - timestamp}ms")
        }

        if (timestamp == lastTimestamp) {
            val seq = sequence.incrementAndGet() and MAX_SEQUENCE
            if (seq == 0L) {
                timestamp = nextMillis(lastTimestamp)
            }
        } else {
            val initSeq = timestamp % 2
            sequence.set(initSeq)
        }

        lastTimestamp = timestamp
        val seq = sequence.get()

        return ((timestamp - EPOCH) shl TIMESTAMP_SHIFT.toInt()) or
                (workerId shl WORKER_SHIFT.toInt()) or
                seq
    }

    private fun nextMillis(lastTimestamp: Long): Long {
        var timestamp = currentTimeMillis()
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis()
        }
        return timestamp
    }

    private fun currentTimeMillis(): Long = System.currentTimeMillis()
}
