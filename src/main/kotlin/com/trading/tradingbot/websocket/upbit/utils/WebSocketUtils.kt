package com.trading.tradingbot.websocket.upbit.utils

import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.UUID
import javax.crypto.spec.SecretKeySpec

@Component
class WebSocketUtils {
    fun generateJwtToken(
        accessKey: String,
        accessSecret: String,
    ): String {
        val payload =
            mapOf(
                "access_key" to accessKey,
                "nonce" to UUID.randomUUID().toString(),
            )

        val secretKeySpec = SecretKeySpec(accessSecret.toByteArray(), "HmacSHA256")

        return Jwts
            .builder()
            .claims(payload)
            .signWith(secretKeySpec, Jwts.SIG.HS256)
            .compact()
    }
}
