package com.github.jingshouyan.sb40k.service.impl

import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.Ticket
import com.github.jingshouyan.sb40k.service.TicketService
import com.github.jingshouyan.sb40k.util.AesGcmUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*

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
        val token = Base64.getEncoder().encodeToString(cipherBytes)

        return token
    }

    override fun varifyToken(token: String): Ticket? {
        try {
            val cipherBytes = Base64.getDecoder().decode(token)
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
            return Ticket(userId, deviceType, ts, deviceId)
        } catch (e: Exception) {
            log.warn("Failed to verify token: $token", e)
            return null
        }
    }

}