package com.github.jingshouyan.sb40k.socketio.cache.impl

import com.github.jingshouyan.sb40k.socketio.cache.ConnectCache
import com.github.jingshouyan.sb40k.socketio.cache.ConnectionInfo
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ConnectCacheMemeryImpl : ConnectCache {

    /**
     * 用户ID -> 设备ID -> 连接信息
     */
    private val cache = ConcurrentHashMap<Long, ConcurrentHashMap<String, ConnectionInfo>>()

    override fun addConnection(info: ConnectionInfo) {
        val inner = cache.computeIfAbsent(info.userId) { ConcurrentHashMap() }
        inner[info.deviceId] = info
    }

    override fun removeConnection(info: ConnectionInfo) {
        cache[info.userId]?.let { inner ->
            inner.remove(info.deviceId)
            if (inner.isEmpty()) {
                cache.remove(info.userId, inner)
            }
        }
    }

    override fun validConnection(info: ConnectionInfo): Boolean {
        val inner = cache[info.userId] ?: return false
        val ci = inner[info.deviceId] ?: return false
        return ci.sessionId == info.sessionId && ci.token == info.token
    }

    override fun getConnections(userIds: List<Long>, excludeToken: String): List<ConnectionInfo> {
        val result = ArrayList<ConnectionInfo>()
        if (userIds.isEmpty()) return result
        for (uid in userIds) {
            val inner = cache[uid] ?: continue
            for (ci in inner.values) {
                // exclude connections whose authToken equals the provided excludeToken
                // protect against nulls just in case (though authToken is non-null in the data class)
                if (excludeToken.isNotEmpty() && ci.token == excludeToken) continue
                result.add(ci)
            }
        }
        return result
    }
}