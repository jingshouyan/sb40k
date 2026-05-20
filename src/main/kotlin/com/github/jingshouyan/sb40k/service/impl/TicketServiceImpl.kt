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
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@Service
class TicketServiceImpl(
    val cfg: BizConfig,
) : TicketService {

    private val log = LoggerFactory.getLogger(TicketServiceImpl::class.java)

    override fun saveTicket(ticket: Ticket): String {
        val data = ticket.userId.toByteArray(StandardCharsets.UTF_8)
        val cipherBytes = AesGcmUtil.encryptBytes(data, cfg.tokenSecret)
        val token = Base58.encode(cipherBytes)
        ticket.token = token
        put(ticket)
        return token
    }

    override fun getTicket(token: String): Ticket? {
        try {
            val cipherBytes = Base58.decode(token)
            val data = AesGcmUtil.decryptBytes(cipherBytes, cfg.tokenSecret)
            val userId = String(data, StandardCharsets.UTF_8)

            val ticket = get(userId, token)
            return ticket
        } catch (e: Exception) {
            log.warn("Failed to verify token:{}", token, e)
            return null
        }
    }

    override fun removeTicket(ticket: Ticket) {
        remove(ticket.userId, ticket.token)
    }

    private val userCache: Cache<String, Cache<String, Ticket>> = Caffeine.newBuilder()
        .expireAfterAccess(cfg.tokenExpireSeconds + 10, TimeUnit.SECONDS)
        .maximumSize(10_000)
        .build()

    fun put(ticket: Ticket) {
        val tc = userCache.get(ticket.userId) {
            tokenCache()
        }
        val ot = tc.asMap().values.filter { it.deviceId == ticket.deviceId }
        log.info("invalidate old ticket {}", ot)
        ot.forEach { tc.invalidate(it.token) }
        tc.put(ticket.token, ticket)
    }

    fun get(userId: String, token: String): Ticket? {
        return userCache.getIfPresent(userId)?.getIfPresent(token)
    }

    fun remove(userId: String, token: String) {
        userCache.asMap().computeIfPresent(userId) { _, deviceCache ->
            deviceCache.invalidate(token)
            if (deviceCache.asMap().isEmpty()) null else deviceCache
        }
    }

    fun tokenCache(): Cache<String, Ticket> {
        val newCache = Caffeine.newBuilder()
            .expireAfterAccess(cfg.tokenExpireSeconds, TimeUnit.SECONDS)
            .maximumSize(10)
            .build<String, Ticket>()
        return newCache
    }
}
