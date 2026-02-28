package com.github.jingshouyan.sb40k.socketio

import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DisconnectListener
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean

@SpringBootConfiguration
class SocketIOConfig {

    private val log = LoggerFactory.getLogger(SocketIOConfig::class.java)

    @Value($$"${socketio.port}")
    private var port: Int = 0

    private lateinit var server: SocketIOServer

    @Bean
    fun socketIOServer(
        authListener: AuthorizationListener,
        connectListener: ConnectListener,
        disconnectListener: DisconnectListener
    ): SocketIOServer {
        log.info("Socket.IO server starting on port $port")
        val config = Configuration()
        config.port = port
        config.authorizationListener = authListener
        log.info("Socket.IO server auth configuration: ${config.authorizationListener}")
        server = SocketIOServer(config)
        server.start()
        log.info("Socket.IO server started on port $port")

        server.addConnectListener(connectListener)
        server.addDisconnectListener(disconnectListener)
        return server
    }


    @PreDestroy
    fun stop() {
        log.info("Socket.IO server stopping")
        server.stop()
        log.info("Socket.IO server stopped")
    }


}


