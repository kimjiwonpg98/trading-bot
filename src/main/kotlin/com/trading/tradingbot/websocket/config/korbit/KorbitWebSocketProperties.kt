package com.trading.tradingbot.websocket.config.korbit

import com.trading.tradingbot.websocket.config.WebSocketSecretValues
import com.trading.tradingbot.websocket.config.korbit.KorbitWebSocketProperties.Companion.KORBIT_WEBSOCKET_CONFIG
import org.springframework.boot.context.properties.ConfigurationProperties
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@ConfigurationProperties(prefix = KORBIT_WEBSOCKET_CONFIG)
class KorbitWebSocketProperties(
    private val apiKey: String,
    private val apiSecret: String,
    private val wsUrl: String,
) {
    fun getKorbitConfig(): WebSocketSecretValues =
        WebSocketSecretValues(
            apiKey = apiKey,
            apiSecret = apiSecret,
            wsUrl = wsUrl,
        )

    fun createHmacSignature(query: String): String {
        val secretKeySpec = SecretKeySpec(this.apiSecret.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)
        val hmacBytes = mac.doFinal(query.toByteArray())
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        const val KORBIT_WEBSOCKET_CONFIG = "spring.websocket.korbit"
    }
}
