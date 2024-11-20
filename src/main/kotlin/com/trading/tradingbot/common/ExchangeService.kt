package com.trading.tradingbot.common

import java.math.BigDecimal

interface ExchangeService {
    fun getCurrentPrice(tradingPair: String): BigDecimal

    fun createLimitBuyOrder(
        tradingPair: String,
        amount: BigDecimal,
        price: BigDecimal,
    ): String

    fun createLimitSellOrder(
        tradingPair: String,
        amount: BigDecimal,
        price: BigDecimal,
    ): String
}
