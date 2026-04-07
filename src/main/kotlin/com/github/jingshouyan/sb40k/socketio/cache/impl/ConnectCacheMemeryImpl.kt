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


    override fun getConnections(userIds: List<Long>): List<ConnectionInfo> {
        val result = mutableListOf<ConnectionInfo>()
        for (userId in userIds) {
            cache[userId]?.let { inner ->
                result.addAll(inner.values)
            }
        }
        return result

    }
}