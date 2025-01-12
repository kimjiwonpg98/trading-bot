package com.trading.tradingbot.bithumb.dto

import com.trading.tradingbot.trading.out.GetBalancesResponse

data class GetBalancesByBithumbDto(
    val currency: String,
    val balance: String,
    val locked: String,
    val avg_buy_price: String,
    val avg_buy_price_modified: Boolean = false,
    val unit_currency: String,
) {
    fun toGetBalancesResponse(): GetBalancesResponse =
        GetBalancesResponse(
            currency = currency,
            balance = balance,
            available = balance,
            tradeInUse = locked,
            withdrawalInUse = "0",
            avgPrice = avg_buy_price,
        )
}
