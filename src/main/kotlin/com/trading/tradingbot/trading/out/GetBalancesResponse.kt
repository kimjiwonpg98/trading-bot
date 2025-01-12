package com.trading.tradingbot.trading.out

data class GetBalancesResponse(
    val currency: String,
    val balance: String,
    val available: String,
    val tradeInUse: String,
    val withdrawalInUse: String,
    val avgPrice: String,
) {
    fun to(
        currency: String,
        balance: String,
        available: String,
        tradeInUse: String,
        withdrawalInUse: String,
        avgPrice: String,
    ): GetBalancesResponse =
        GetBalancesResponse(
            currency = currency,
            balance = balance,
            available = available,
            tradeInUse = tradeInUse,
            withdrawalInUse = withdrawalInUse,
            avgPrice = avgPrice,
        )
}
