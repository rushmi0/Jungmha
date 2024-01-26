package org.jungmha.routes.api.v1.websockets

import io.micronaut.websocket.CloseReason
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.OnClose
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.OnOpen
import io.micronaut.websocket.annotation.ServerWebSocket
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//@ServerWebSocket("/")
class SimpleWebSocketServer {

    @OnOpen
    fun onOpen(session: WebSocketSession): Publisher<String> {
        LOG.info("Session open: $session")
        return session.send("Connected!")
    }

    @OnMessage
    fun onMessage(message: String, session: WebSocketSession): Publisher<String> {
        LOG.info("Received message: $message from session ${session.id}")
        if (message.contentEquals("disconnect me")) {
            LOG.info("Client close requested!")
            session.close(CloseReason.NORMAL)
            return Flowable.empty()
        }

        for (i in 0 .. 20) {
            LOG.info("Sending message: $message to session ${session.id}")
            return session.send(message)
        }
        return session.send("Not supported => ($message)")
    }

    @OnClose
    fun onClose(session: WebSocketSession) {
        LOG.info("Session closed ${session.id}")
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(SimpleWebSocketServer::class.java)
    }

}
