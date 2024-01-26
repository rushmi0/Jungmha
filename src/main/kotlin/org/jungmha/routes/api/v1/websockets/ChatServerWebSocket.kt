//package org.jungmha.routes.api.v1.websockets
//
//import io.micronaut.http.annotation.Produces
//import io.micronaut.websocket.WebSocketBroadcaster
//import io.micronaut.websocket.WebSocketSession
//import io.micronaut.websocket.annotation.OnClose
//import io.micronaut.websocket.annotation.OnMessage
//import io.micronaut.websocket.annotation.OnOpen
//import io.micronaut.websocket.annotation.ServerWebSocket
//
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//
//import java.util.function.Predicate
//
//@ServerWebSocket("/")
//class ChatServerWebSocket(
//    private val broadcaster: WebSocketBroadcaster
//) {
//
//    @OnOpen
//    @Produces("application/nostr+json")
//    fun onOpen(topic: String, username: String, session: WebSocketSession) {
//        val msg = "[$username] Joined!"
//        LOG.info(msg)
//        broadcaster.broadcastSync(msg, isValid(topic, session))
//    }
//
//    @OnMessage
//    fun onMessage(
//        topic: String, username: String,
//        message: String, session: WebSocketSession
//    ) {
//        val msg = "[$username] $message"
//        LOG.info(msg)
//        broadcaster.broadcastSync(msg, isValid(topic, session))
//    }
//
//    @OnClose
//    fun onClose(topic: String, username: String, session: WebSocketSession) {
//        val msg = "[$username] Disconnected!"
//        LOG.info(msg)
//        broadcaster.broadcastSync(msg, isValid(topic, session))
//    }
//
//    private fun isValid(topic: String, session: WebSocketSession): Predicate<WebSocketSession> {
//        return Predicate<WebSocketSession> {
//            (it !== session && topic.equals(it.uriVariables.get("topic", String::class.java, null), ignoreCase = true))
//        }
//    }
//
//    companion object {
//        private val LOG: Logger = LoggerFactory.getLogger(ChatServerWebSocket::class.java)
//    }
//
//}