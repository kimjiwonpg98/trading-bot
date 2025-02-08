package com.trading.tradingbot.bithumb.event

import com.trading.tradingbot.trading.out.GetTickerResponse

data class BitumbTickerEvent(
    val ticketEvent: GetTickerResponse,
)
