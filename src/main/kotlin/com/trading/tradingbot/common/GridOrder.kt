package com.trading.tradingbot.common

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class GridOrder(
    val id: UUID = UUID.randomUUID(),
    val type: OrderType,
    val price: BigDecimal,
    val amount: BigDecimal,
    val timestamp: Instant = Instant.now(),
)
