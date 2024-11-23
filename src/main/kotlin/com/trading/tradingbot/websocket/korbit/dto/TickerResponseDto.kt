package com.trading.tradingbot.websocket.korbit.dto

import java.math.BigDecimal

data class KorbitTickerResponseDto(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val prevClose: BigDecimal,
    val priceChange: BigDecimal,
    val priceChangePercent: BigDecimal,
    val volume: BigDecimal,
    val quoteVolume: BigDecimal,
    val bestAskPrice: BigDecimal,
    val bestBidPrice: BigDecimal,
    val lastTradedAt: Long,
)
