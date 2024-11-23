package com.trading.tradingbot.common

import java.math.BigDecimal

data class InfinityGridBotConfig(
    val symbol: String,
    val initialPrice: BigDecimal,
    val gridLowerBound: BigDecimal,
    val gridCount: Int,
    val gridPercent: BigDecimal,
)
