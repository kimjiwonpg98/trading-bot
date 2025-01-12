package com.trading.tradingbot.grid

import java.math.BigDecimal

data class InfinityGridBotConfig(
    val symbol: String,
    val initialPrice: BigDecimal,
    val gridLowerBound: BigDecimal,
    val gridCount: Int,
    val gridPercent: BigDecimal,
)
