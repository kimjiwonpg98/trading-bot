package com.trading.tradingbot.grid

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class GridOrder(
    val id: UUID = UUID.randomUUID(),
    val type: OrderType,
    val price: BigDecimal,
    val amount: BigDecimal,
    val timestamp: Instant = Instant.now(),
)
