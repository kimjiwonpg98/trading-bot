package com.trading.tradingbot.trading

import com.trading.tradingbot.trading.`in`.CreateLimitOrderRequestParams
import com.trading.tradingbot.trading.`in`.CreateMakerOrderRequestParams
import com.trading.tradingbot.trading.out.GetBalancesResponse
import com.trading.tradingbot.trading.out.GetOpenOrdersResponse

interface TradingService {
    fun createLimitOrder(params: CreateLimitOrderRequestParams)

    fun createMakerOrder(params: CreateMakerOrderRequestParams)

    fun getBalances(currency: String): List<GetBalancesResponse>

    fun getOpenOrder(symbol: String): List<GetOpenOrdersResponse>
}
