package com.github.jingshouyan.sb40k.socketio.cache

interface ConnectCache {

    fun addConnection(info: ConnectionInfo)

    fun removeConnection(info: ConnectionInfo)

    fun getConnections(userIds: List<Long>, excludeToken: String): List<ConnectionInfo>
}