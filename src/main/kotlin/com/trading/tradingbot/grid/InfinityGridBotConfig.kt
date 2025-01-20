package com.trading.tradingbot.grid

import java.math.BigDecimal

data class InfinityGridBotConfig(
    val symbol: String,
    val currency: String,
    val initialAmount: BigDecimal,
    val gridLowerBound: BigDecimal,
    val gridCount: Int,
    val gridPercent: BigDecimal,
    val initialInvestment: BigDecimal,
)
