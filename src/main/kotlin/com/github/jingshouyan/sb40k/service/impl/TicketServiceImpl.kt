package com.github.jingshouyan.sb40k.service.impl

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.Ticket
import com.github.jingshouyan.sb40k.service.TicketService
import com.github.jingshouyan.sb40k.util.AesGcmUtil
import com.github.jingshouyan.sb40k.util.Base58
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@Service
class TicketServiceImpl(
    val cfg: BizConfig,
) : TicketService {

    private val log = LoggerFactory.getLogger(TicketServiceImpl::class.java)

    override fun saveTicket(ticket: Ticket): String {
        val deviceIdBytes = ticket.deviceId.toByteArray(StandardCharsets.UTF_8)
        val version = 1.toByte()
        val buf = ByteBuffer.allocate(
            1 + 8 + 1 + 8 + 4 + deviceIdBytes.size
        )
        buf.put(version)
        buf.putLong(ticket.userId)
        buf.put(ticket.deviceType)
        buf.putLong(ticket.ts)
        buf.putInt(deviceIdBytes.size)
        buf.put(deviceIdBytes)
        val data = buf.array()
        val cipherBytes = AesGcmUtil.encryptBytes(data, cfg.tokenSecret)
        val token = Base58.encode(cipherBytes)

        // put cache
        put(ticket)

        return token
    }

    override fun varifyToken(token: String): Ticket? {
        try {
            val cipherBytes = Base58.decode(token)
            val data = AesGcmUtil.decryptBytes(cipherBytes, cfg.tokenSecret)
            val buf = ByteBuffer.wrap(data)
            val version = buf.get()
            if (version != 1.toByte()) {
                return null
            }
            val userId = buf.long
            val deviceType = buf.get()
            val ts = buf.long
            val deviceIdLen = buf.int
            if (deviceIdLen !in 0..1024) {
                return null
            }
            val deviceIdBytes = ByteArray(deviceIdLen)
            buf.get(deviceIdBytes)
            val deviceId = String(deviceIdBytes, StandardCharsets.UTF_8)
            // use cache to verify token
            val ticket = get(userId, deviceId)

            return ticket
        } catch (e: Exception) {
            log.warn("Failed to verify token: $token", e)
            return null
        }
    }

    private val userCache: Cache<Long, Cache<String, Ticket>> = Caffeine.newBuilder()
        .expireAfterAccess(cfg.tokenExpireSeconds + 10, TimeUnit.SECONDS)
        .maximumSize(10_000)
        .build()

    fun put(ticket: Ticket) {
        userCache.get(ticket.userId) {
            newDeviceCache()
        }.put(ticket.deviceId, ticket)
    }

    fun get(userId: Long, deviceId: String): Ticket? {
        return userCache.getIfPresent(userId)?.getIfPresent(deviceId)
    }

    fun remove(userId: Long, deviceId: String) {
        userCache.asMap().computeIfPresent(userId) { _, deviceCache ->
            deviceCache.invalidate(deviceId)
            if (deviceCache.asMap().isEmpty()) null else deviceCache
        }
    }

    fun newDeviceCache(): Cache<String, Ticket> {
        val newCache = Caffeine.newBuilder()
            .expireAfterAccess(cfg.tokenExpireSeconds, TimeUnit.SECONDS)
            .maximumSize(10)
            .build<String, Ticket>()
        return newCache
    }
}