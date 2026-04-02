package com.github.jingshouyan.sb40k.socketio.listener.data

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.github.jingshouyan.sb40k.socketio.listener.NamedDataListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EchoListener : NamedDataListener<String> {

    private val log = LoggerFactory.getLogger(EchoListener::class.java)

    override fun eventName() = "echo"

    override fun onData(client: SocketIOClient?, data: String?, ackSender: AckRequest?) {
        log.info("Received echo data from client: {}, data: {}", client?.sessionId, data)
        ackSender?.sendAckData("Echo: $data", client?.get<Any>("connectionInfo"))
    }
}