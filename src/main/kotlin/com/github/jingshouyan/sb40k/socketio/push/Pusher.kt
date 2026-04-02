package com.github.jingshouyan.sb40k.socketio.push

import com.corundumstudio.socketio.SocketIOServer
import com.github.jingshouyan.sb40k.socketio.cache.ConnectCache
import org.springframework.stereotype.Component

@Component
class Pusher(val connectCache: ConnectCache, val socketIOServer: SocketIOServer) {


    fun push(userIds: List<Long>, excludeToken: String, event: String, data: Any) {
        val connections = connectCache.getConnections(userIds, excludeToken)
        for (ci in connections) {
            socketIOServer.getClient(ci.sessionId)?.sendEvent(event, data)
        }
    }
}