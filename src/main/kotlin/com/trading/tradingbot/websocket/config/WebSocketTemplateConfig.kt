package com.trading.tradingbot.websocket.config

import com.trading.tradingbot.websocket.config.korbit.KorbitWebSocketProperties
import com.trading.tradingbot.websocket.config.upbit.UpbitWebSocketProperties
import com.trading.tradingbot.websocket.korbit.handler.KorbitWebSocketHandler
import com.trading.tradingbot.websocket.upbit.handler.UpbitWebSocketHandler
import com.trading.tradingbot.websocket.upbit.utils.WebSocketUtils
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import java.net.URI

@Configuration
@EnableConfigurationProperties(value = [UpbitWebSocketProperties::class, KorbitWebSocketProperties::class])
class WebSocketTemplateConfig(
    private val upbitWebSocketProperties: UpbitWebSocketProperties,
    private val korbitWebSocketProperties: KorbitWebSocketProperties,
    private val webSocketUtils: WebSocketUtils,
    private val upbitWebSocketHandler: UpbitWebSocketHandler,
    private val korbitWebSocketHandler: KorbitWebSocketHandler,
) {
    @Bean(name = [UPBIT_WEBSOCKET_CONFIG])
    fun upbitWebSocketConnectionManager(): WebSocketConnectionManager? {
        if (!upbitWebSocketProperties.isEnabled()) return null

        val token =
            webSocketUtils.generateJwtToken(
                upbitWebSocketProperties.getUpbitConfig().apiKey,
                upbitWebSocketProperties.getUpbitConfig().apiSecret,
            )

        val headers =
            WebSocketHttpHeaders().apply {
                set("Authorization", "Bearer $token")
            }

        val webSocketClient = StandardWebSocketClient()
        val session = WebSocketConnectionManager(webSocketClient, upbitWebSocketHandler, upbitWebSocketProperties.getUpbitConfig().wsUrl)
        session.headers = headers
        session.isAutoStartup = true
        return session
    }

    @Bean(name = [KORBIT_WEBSOCKET_CONFIG])
    fun korbitWebSocketConnectionManager(): WebSocketConnectionManager? {
        if (!korbitWebSocketProperties.isEnabled()) return null

        val timestamp = System.currentTimeMillis()
        val params = "timestamp=$timestamp"
        val signature = korbitWebSocketProperties.createHmacSignature(params)
        val urlWithParams = "${korbitWebSocketProperties.getKorbitConfig().wsUrl}?$params&signature=$signature"
        val headers =
            WebSocketHttpHeaders().apply {
                set("X-KAPI-KEY", korbitWebSocketProperties.getKorbitConfig().apiKey)
            }

        val webSocketClient = StandardWebSocketClient()
        val session = WebSocketConnectionManager(webSocketClient, korbitWebSocketHandler, URI(urlWithParams))
        session.headers = headers
        session.isAutoStartup = true
        return session
    }

    companion object {
        private const val UPBIT_WEBSOCKET_CONFIG = "upbitWebSocketConfig"
        private const val KORBIT_WEBSOCKET_CONFIG = "korbitWebSocketConfig"
    }
}
