package com.trading.tradingbot.korbit.dto.balances

data class BalancesResponseDto(
    val success: Boolean,
    val data: List<BalancesData>,
)
