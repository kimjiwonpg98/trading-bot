package com.trading.tradingbot.trading.`in`

data class CreateLimitOrderRequestParams(
    val qty: String,
    val symbol: String,
    val side: String,
    val price: String,
)
