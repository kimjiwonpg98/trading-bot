package com.trading.tradingbot.websocket.korbit.handler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.trading.tradingbot.websocket.korbit.dto.KorbitTickerResponseDto
import com.trading.tradingbot.websocket.korbit.event.KorbitWebSocketTickerEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession

@Component
class KorbitWebSocketHandler(
    private val eventPublisher: ApplicationEventPublisher,
) : WebSocketHandler {
    private var session: WebSocketSession? = null

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val tickerSubscribeMessage =
            TextMessage(
                "[{\"method\":\"subscribe\",\"type\":\"ticker\",\"symbols\":[\"btc_krw\",\"eth_krw\"]}]",
            )
        session.sendMessage(tickerSubscribeMessage)
    }

    // 메시지를 처리하는 로직
    override fun handleMessage(
        session: WebSocketSession,
        message: WebSocketMessage<*>,
    ) {
        val objectMapper = ObjectMapper()
        val payload = message.payload as String

        // 바이트 배열을 문자열로 변환
        val json: JsonNode = objectMapper.readTree(payload)
        val type = json.get("type").asText()

        if (type == "ticker") {
            val data = json.get("data").asText()
            val result = objectMapper.readValue(data, KorbitTickerResponseDto::class.java)
            eventPublisher.publishEvent(KorbitWebSocketTickerEvent(ticketEvent = result))
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
