package com.trading.tradingbot.grid

import java.math.BigDecimal

data class GridBotConfig(
    val symbol: String,
    val initialPrice: BigDecimal,
    val gridUpperBound: BigDecimal,
    val gridLowerBound: BigDecimal,
    val gridCount: BigDecimal,
    val investmentAmount: BigDecimal,
)
