package com.trading.tradingbot.upbit.dto

data class GetMarketsDto(
    val market: String,
    val korean_name: String,
    val english_name: String
)
