package com.trading.tradingbot.websocket.upbit.event

import com.trading.tradingbot.websocket.upbit.dto.TickerResponseDto

data class UpbitWebSocketTickerEvent(
    val ticketEvent: TickerResponseDto,
)
