package com.trading.tradingbot.trading.`in`

data class CreateMakerOrderRequestParams(
    val amount: String,
    val symbol: String,
    val side: String,
)
