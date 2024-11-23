package com.trading.tradingbot.websocket.korbit.event

import com.trading.tradingbot.websocket.korbit.dto.KorbitTickerResponseDto

data class KorbitWebSocketTickerEvent(
    val ticketEvent: KorbitTickerResponseDto,
)
