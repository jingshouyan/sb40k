package com.github.jingshouyan.sb40k.socketio.listener

import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.AuthorizationResult
import com.corundumstudio.socketio.HandshakeData
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DisconnectListener
import com.github.jingshouyan.sb40k.socketio.cache.ConnectionInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class AuthListener : AuthorizationListener {

    private val log = LoggerFactory.getLogger(AuthListener::class.java)

    override fun getAuthorizationResult(data: HandshakeData?): AuthorizationResult? {
        log.info("Socket.IO auth listener called with handshake data: $data")
        val token = data?.urlParams?.get("token")?.get(0)
        val params = HashMap<String, Any>()
        params["token"] = token ?: "null"
        if (token == "1") {
            data.authToken = "$token-auth"

            return AuthorizationResult(true, params)
        }
        params["error"] = "Invalid token"
        return AuthorizationResult(false, params)
    }
}

@Component
class ConnListener : ConnectListener {
    private val log = LoggerFactory.getLogger(ConnListener::class.java)

    override fun onConnect(client: SocketIOClient?) {
        client?.get<ConnectionInfo>("info")
        log.info("Client connected: ${client?.sessionId}@${client?.remoteAddress}")
    }
}

@Component
class DisConnListener : DisconnectListener {
    private val log = LoggerFactory.getLogger(DisConnListener::class.java)

    override fun onDisconnect(client: SocketIOClient?) {
        log.info("Client disconnected: ${client?.sessionId}@${client?.remoteAddress}")
    }
}