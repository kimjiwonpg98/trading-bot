package com.trading.tradingbot.upbit.dto

data class GetOrderbookByMarketsDto(
    val market: String,
    val timestamp: Long,
    val total_ask_size: Long,
    val total_bid_size: Long,
    val orderbook_units: Array<OrderbookUnits>
)

data class OrderbookUnits (
    val ask_price: Double, // 매도호가
    val bid_price: Double, // 매수호가
    val ask_size: Double, // 매도 잔량
    val bid_size: Double, // 매수 잔량
)
