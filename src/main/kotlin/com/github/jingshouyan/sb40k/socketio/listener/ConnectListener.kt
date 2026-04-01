package com.github.jingshouyan.sb40k.socketio.listener

import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.AuthorizationResult
import com.corundumstudio.socketio.HandshakeData
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DisconnectListener
import com.corundumstudio.socketio.listener.PongListener
import com.github.jingshouyan.sb40k.entity.Ticket
import com.github.jingshouyan.sb40k.service.TicketService
import com.github.jingshouyan.sb40k.socketio.cache.ConnectCache
import com.github.jingshouyan.sb40k.socketio.cache.ConnectionInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class AuthListener(val ticketService: TicketService) : AuthorizationListener {

    private val log = LoggerFactory.getLogger(AuthListener::class.java)

    override fun getAuthorizationResult(data: HandshakeData?): AuthorizationResult? {
        val token = data?.urlParams?.get("token")?.get(0)
        if (token != null) {
            val ticket = ticketService.getTicket(token)
            if (ticket != null) {
                log.info("Token verified successfully. Ticket: {}", ticket)

                return AuthorizationResult(true, mapOf("ticket" to ticket))

            } else {
                log.warn("Token verification failed for token: {}", token)
            }
        }

        return AuthorizationResult(false)
    }
}

@Component
class ConnListener(val cache: ConnectCache) : ConnectListener {
    private val log = LoggerFactory.getLogger(ConnListener::class.java)

    override fun onConnect(client: SocketIOClient) {
        val ticket = client.get<Ticket>("ticket")
        if (ticket != null) {
            val connectionInfo = ConnectionInfo(
                sessionId = client.sessionId,
                remoteAddress = client.remoteAddress.toString(),
                token = ticket.token,
                userId = ticket.userId,
                deviceType = ticket.deviceType,
                deviceId = ticket.deviceId
            )
            cache.addConnection(connectionInfo)
            client.set("connectionInfo", connectionInfo)
            log.info("Client connected: {}", connectionInfo)
        } else {
            log.warn("No ticket found in client attributes for session: ${client.sessionId}")
            client.disconnect()
        }

    }
}

@Component
class DisConnListener(val cache: ConnectCache) : DisconnectListener {
    private val log = LoggerFactory.getLogger(DisConnListener::class.java)

    override fun onDisconnect(client: SocketIOClient) {
        val connectionInfo = client.get<ConnectionInfo>("connectionInfo")
        if (connectionInfo != null) {
            cache.removeConnection(connectionInfo)
            log.info("Client disconnected: {}", connectionInfo)
        } else {
            log.info(
                "Client disconnected: {}@{}, no connection info found",
                client.sessionId,
                client.remoteAddress
            )
        }
    }
}

@Component
class PongCheckListener(val ticketService: TicketService) : PongListener {
    private val log = LoggerFactory.getLogger(PongCheckListener::class.java)

    override fun onPong(client: SocketIOClient) {
        log.info("Received pong from client: {}", client.sessionId)
        val connectionInfo = client.get<ConnectionInfo>("connectionInfo")
        if (connectionInfo != null) {
            if (ticketService.getTicket(connectionInfo.token) == null) {
                log.warn("Connection info invalid for client: {}, disconnecting", client.sessionId)
                client.disconnect()
            }
        } else {
            log.warn("No connection info found for client: {}, disconnecting", client.sessionId)
            client.disconnect()
        }
    }
}
