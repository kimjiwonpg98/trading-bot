package com.trading.tradingbot.korbit.dto.balances

import com.trading.tradingbot.trading.out.GetBalancesResponse

data class BalancesData(
    val currency: String,
    val balance: String,
    val available: String,
    val tradeInUse: String,
    val withdrawalInUse: String,
    val avgPrice: String,
) {
    fun toGetBalancesResponse(): GetBalancesResponse =
        GetBalancesResponse(
            currency = currency,
            balance = balance,
            available = available,
            tradeInUse = tradeInUse,
            withdrawalInUse = withdrawalInUse,
            avgPrice = avgPrice,
        )
}
