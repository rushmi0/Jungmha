//package org.jungmha.routes.api.v1.websockets
//
//import io.micronaut.context.annotation.Bean
//import io.micronaut.websocket.WebSocketBroadcaster
//import io.micronaut.websocket.WebSocketSession
//import io.micronaut.websocket.annotation.OnClose
//import io.micronaut.websocket.annotation.OnMessage
//import io.micronaut.websocket.annotation.OnOpen
//import io.micronaut.websocket.annotation.ServerWebSocket
//import io.micronaut.websocket.context.WebSocketBean
//import jakarta.inject.Inject
//
//@Bean
//@ServerWebSocket("/chat/{username}")
//class ChatWebSocketController @Inject constructor(
//    private val broadcaster: WebSocketBroadcaster<Any>
//) : WebSocketBean {
//
//    @OnOpen
//    fun onOpen(username: String, session: WebSocketSession) {
//        // บันทึก WebSocketSession ของผู้ใช้
//        session.put("username", username)
//    }
//
//    @OnMessage
//    fun onMessage(username: String, message: String, session: WebSocketSession) {
//        val senderUsername = session.get<String>("username").orElse("Unknown")
//
//        // ส่งข้อความไปยัง WebSocketSession ของผู้รับ (B)
//        val receiverSession = broadcaster.findSession("B")
//        if (receiverSession.isPresent) {
//            val receiverUsername = receiverSession.get().get<String>("username").orElse("Unknown")
//            broadcaster.sendSync(receiverSession.get(), "$senderUsername -> $receiverUsername: $message")
//        }
//    }
//
//    @OnClose
//    fun onClose(username: String, session: WebSocketSession) {
//        // ลบ WebSocketSession ของผู้ใช้เมื่อปิดการเชื่อมต่อ
//        session.remove("username")
//    }
//}
