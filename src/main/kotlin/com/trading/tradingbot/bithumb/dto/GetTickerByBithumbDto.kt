package com.trading.tradingbot.bithumb.dto

import com.trading.tradingbot.trading.out.GetTickerResponse
import java.math.BigDecimal

data class GetTickerByBithumbDto(
    val market: String, // 종목 구분 코드
    val trade_date: String, // 최근 거래 일자(UTC) 포맷: yyyyMMdd
    val trade_time: String, // 최근 거래 시각(UTC) 포맷: HHmmss
    val trade_date_kst: String, // 최근 거래 일자(KST) 포맷: yyyyMMdd
    val trade_time_kst: String, // 최근 거래 시각(KST) 포맷: HHmmss
    val trade_timestamp: Long, // 최근 거래 일시(UTC) 포맷: Unix Timestamp
    val opening_price: Double, // 시가
    val high_price: Double, // 고가
    val low_price: Double, // 저가
    val trade_price: Double, // 종가(현재가)
    val prev_closing_price: Double, // 전일 종가(KST 0시 기준)
    val change: String, // EVEN: 보합, RISE: 상승, FALL: 하락
    val change_price: Double, // 변화액의 절대값
    val change_rate: Double, // 변화율의 절대값
    val signed_change_price: Double, // 부호가 있는 변화액
    val signed_change_rate: Double, // 부호가 있는 변화율
    val trade_volume: Double, // 가장 최근 거래량
    val acc_trade_price: Double, // 누적 거래대금(KST 0시 기준)
    val acc_trade_price_24h: Double, // 24시간 누적 거래대금
    val acc_trade_volume: Double, // 누적 거래량(KST 0시 기준)
    val acc_trade_volume_24h: Double, // 24시간 누적 거래량
    val highest_52_week_price: Double, // 52주 신고가
    val highest_52_week_date: String, // 52주 신고가 달성일 포맷: yyyy-MM-dd
    val lowest_52_week_price: Double, // 52주 신저가
    val lowest_52_week_date: String, // 52주 신저가 달성일 포맷: yyyy-MM-dd
    val timestamp: Long, // 타임스탬프
) {
    fun toGetTickerResponse() =
        GetTickerResponse(
            close = BigDecimal(trade_price),
        )
}
