package com.trading.tradingbot.upbit.dto

data class GetTickerByMarketsDto(
    val market: String,
    val trade_date: String,
    val trade_time: String,
    val opening_price: Double,
    val high_price: Double,
    val low_price: Double,
    val trade_price: Double,
    val prev_closing_price: Double
)


