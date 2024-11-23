package com.trading.tradingbot.websocket.upbit.handler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.trading.tradingbot.websocket.upbit.dto.TickerResponseDto
import com.trading.tradingbot.websocket.upbit.event.UpbitWebSocketTickerEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import java.nio.ByteBuffer
import java.util.UUID

@Component
class UpbitWebSocketHandler(
    private val eventPublisher: ApplicationEventPublisher,
) : WebSocketHandler {
    private var session: WebSocketSession? = null

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uuid = UUID.randomUUID().toString()
        val tickerSubscribeMessage =
            TextMessage(
                """
            [
              {
                "ticket": "$uuid"
              },
              {
                "type": "ticker",
                "codes": [
                  "KRW-BTC"
                ]
              },
              {
                "format": "DEFAULT"
              }
            ]
        """,
            )

        session.sendMessage(tickerSubscribeMessage)
    }

    // 메시지를 처리하는 로직
    override fun handleMessage(
        session: WebSocketSession,
        message: WebSocketMessage<*>,
    ) {
        val objectMapper = ObjectMapper()
        val payload = message.payload as ByteBuffer
        val byteArray = ByteArray(payload.remaining())
        payload.get(byteArray)

        // 바이트 배열을 문자열로 변환
        val jsonString = String(byteArray)
        val json: JsonNode = objectMapper.readTree(jsonString)
        val type = json.get("type").asText()

        if (type == "ticker") {
            val result = objectMapper.readValue(jsonString, TickerResponseDto::class.java)
            eventPublisher.publishEvent(UpbitWebSocketTickerEvent(ticketEvent = result))
        }
    }

    // 오류 처리
    override fun handleTransportError(
        session: WebSocketSession,
        exception: Throwable,
    ) {
    }

    // 연결 종료 처리
    override fun afterConnectionClosed(
        session: WebSocketSession,
        closeStatus: CloseStatus,
    ) {
    }

    override fun supportsPartialMessages(): Boolean = false

    // 이 메서드에서 다른 곳에서도 메시지를 보낼 수 있게 함
    fun sendMessageToClient(message: String) {
        session?.sendMessage(TextMessage(message))
    }
}
