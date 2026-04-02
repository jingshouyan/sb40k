package com.github.jingshouyan.sb40k.socketio.listener

import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import org.springframework.core.ResolvableType

interface NamedDataListener<T> : DataListener<T> {

    fun eventName(): String

    @Suppress("UNCHECKED_CAST")
    fun dataClass(): Class<T> {
        val type = ResolvableType.forClass(this.javaClass)
            .`as`(NamedDataListener::class.java)
            .getGeneric(0)
            .resolve()

        return type as? Class<T>
            ?: throw IllegalStateException("Cannot resolve generic type for $this")
    }

    fun register(server: SocketIOServer) {
        server.addEventListener(eventName(), dataClass(), this)
    }


}