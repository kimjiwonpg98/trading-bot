package com.trading.tradingbot.bithumb.dto

import com.trading.tradingbot.trading.out.GetOpenOrdersResponse
import com.trading.tradingbot.utils.ConvertorUtils

data class GetOpenOrdersByBithumbDto(
    val uuid: String, // 주문의 고유 아이디
    val side: String, // 주문 종류
    val ord_type: String, // 주문 방식
    val price: String, // 주문 당시 화폐 가격
    val state: String, // 주문 상태
    val market: String, // 마켓의 유일키
    val created_at: String, // 주문 생성 시간
    val volume: String, // 사용자가 입력한 주문 양
    val remaining_volume: String, // 체결 후 남은 주문 양
    val reserved_fee: String, // 수수료로 예약된 비용
    val remaining_fee: String, // 남은 수수료
    val paid_fee: String, // 사용된 수수료
    val locked: String, // 거래에 사용중인 비용
    val executed_volume: String, // 체결된 양
    val trades_count: Int, // 해당 주문에 걸린 체결 수
) {
    fun toGetOpenOrdersResponse() =
        GetOpenOrdersResponse(
            side = ConvertorUtils.convertSide(side, "bithumb"),
            orderType = ord_type,
            status = state,
        )
}
