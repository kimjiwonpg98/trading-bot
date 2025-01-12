package com.trading.tradingbot.korbit.dto.orders

import com.trading.tradingbot.trading.out.GetOpenOrdersResponse

data class OpenOrdersResponseData(
    val orderType: String,
    val side: String,
    val status: String,
) {
    fun toGetOpenOrderResponse(): GetOpenOrdersResponse =
        GetOpenOrdersResponse(
            orderType = orderType,
            side = side,
            status = status,
        )
}
