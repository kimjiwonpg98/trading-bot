package com.trading.tradingbot.websocket.upbit.services

import com.trading.tradingbot.websocket.config.WebSocketConfig
import org.springframework.stereotype.Service

@Service
class WebSocketService(
    private val webSocketConfig: WebSocketConfig,
) {
    fun useConfig() {
        println(webSocketConfig)
    }

    companion object {
    }
}
