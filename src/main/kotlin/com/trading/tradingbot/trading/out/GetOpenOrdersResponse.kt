package com.trading.tradingbot.trading.out

data class GetOpenOrdersResponse(
    val orderType: String,
    val side: String,
    val status: String?,
)
