package com.trading.tradingbot.korbit.dto.orders

data class OpenOrdersResponseDto(
    val success: Boolean,
    val data: List<OpenOrdersResponseData>,
)
