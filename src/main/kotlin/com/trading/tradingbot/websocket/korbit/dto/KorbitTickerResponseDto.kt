package com.trading.tradingbot.websocket.korbit.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class KorbitTickerResponseDto(
    @JsonProperty("open") val open: BigDecimal,
    @JsonProperty("high") val high: BigDecimal,
    @JsonProperty("low") val low: BigDecimal,
    @JsonProperty("close") val close: BigDecimal,
    @JsonProperty("prevClose") val prevClose: BigDecimal,
    @JsonProperty("priceChange") val priceChange: BigDecimal,
    @JsonProperty("priceChangePercent") val priceChangePercent: BigDecimal,
    @JsonProperty("volume") val volume: BigDecimal,
    @JsonProperty("quoteVolume") val quoteVolume: BigDecimal,
    @JsonProperty("bestAskPrice") val bestAskPrice: BigDecimal,
    @JsonProperty("bestBidPrice") val bestBidPrice: BigDecimal,
    @JsonProperty("lastTradedAt") val lastTradedAt: Long,
)
