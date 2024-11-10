package com.trading.tradingbot.websocket.config.upbit

import com.trading.tradingbot.websocket.config.WebSocketSecretValues
import com.trading.tradingbot.websocket.config.upbit.UpbitWebSocketProperties.Companion.UPBIT_WEBSOCKET_CONFIG
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = UPBIT_WEBSOCKET_CONFIG)
data class UpbitWebSocketProperties(
    private val apiKey: String,
    private val apiSecret: String,
    private val wsUrl: String,
) {
    fun getUpbitConfig(): WebSocketSecretValues =
        WebSocketSecretValues(
            apiKey = apiKey,
            apiSecret = apiSecret,
            wsUrl = wsUrl,
        )

    companion object {
        const val UPBIT_WEBSOCKET_CONFIG = "spring.websocket.upbit"
    }
}
