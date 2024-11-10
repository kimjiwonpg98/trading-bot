package com.trading.tradingbot.websocket.upbit.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class TickerResponseDto
    @JsonCreator
    constructor(
        @JsonProperty("type") val type: String, // 종목 타입
        @JsonProperty("code") val code: String, // 마켓 코드 (예: KRW-BTC)
        @JsonProperty("opening_price") val openingPrice: BigDecimal, // 시가
        @JsonProperty("high_price") val highPrice: BigDecimal, // 고가
        @JsonProperty("low_price") val lowPrice: BigDecimal, // 저가
        @JsonProperty("trade_price") val tradePrice: BigDecimal, // 현재가
        @JsonProperty("prev_closing_price") val prevClosingPrice: BigDecimal, // 전일 종가
        @JsonProperty("change") val change: String, // 전일 대비 변화 (RISE: 상승, FALL: 하락, EVEN: 보합)
        @JsonProperty("change_price") val changePrice: BigDecimal, // 전일 대비 변화 값
        @JsonProperty("signed_change_price") val signedChangePrice: BigDecimal, // 전일 대비 부호 있는 변화 값
        @JsonProperty("change_rate") val changeRate: BigDecimal, // 전일 대비 변화율
        @JsonProperty("signed_change_rate") val signedChangeRate: BigDecimal, // 전일 대비 부호 있는 변화율
        @JsonProperty("trade_volume") val tradeVolume: BigDecimal, // 가장 최근 거래량
        @JsonProperty("acc_trade_volume") val accTradeVolume: BigDecimal, // 누적 거래량 (UTC 0시 기준)
        @JsonProperty("acc_trade_volume_24h") val accTradeVolume24h: BigDecimal, // 24시간 누적 거래량
        @JsonProperty("acc_trade_price") val accTradePrice: BigDecimal, // 누적 거래대금 (UTC 0시 기준)
        @JsonProperty("acc_trade_price_24h") val accTradePrice24h: BigDecimal, // 24시간 누적 거래대금
        @JsonProperty("trade_date") val tradeDate: String, // 최근 거래 일자 (UTC 기준) (yyyyMMdd 형식)
        @JsonProperty("trade_time") val tradeTime: String, // 최근 거래 시각 (UTC 기준) (HHmmss 형식)
        @JsonProperty("trade_timestamp") val tradeTimestamp: Long, // 거래 타임스탬프 (밀리초 단위)
        @JsonProperty("ask_bid") val askBid: String, // 매도/매수 구분 (ASK: 매도, BID: 매수)
        @JsonProperty("acc_ask_volume") val accAskVolume: BigDecimal, // 누적 매도량
        @JsonProperty("acc_bid_volume") val accBidVolume: BigDecimal, // 누적 매수량
        @JsonProperty("highest_52_week_price") val highest52WeekPrice: BigDecimal, // 52주 최고가
        @JsonProperty("highest_52_week_date") val highest52WeekDate: String, // 52주 최고가 달성일 (yyyy-MM-dd 형식)
        @JsonProperty("lowest_52_week_price") val lowest52WeekPrice: BigDecimal, // 52주 최저가
        @JsonProperty("lowest_52_week_date") val lowest52WeekDate: String, // 52주 최저가 달성일 (yyyy-MM-dd 형식)
        @JsonProperty("market_state") val marketState: String, // 거래상태 (ACTIVE: 거래 가능, PREVIEW: 입금 지원 등)
        @JsonProperty("delisting_date") val delistingDate: String? = null, // 거래지원 종료일
        @JsonProperty("market_warning") val marketWarning: String, // 유의 종목 여부 (NONE, CAUTION 등)
        @JsonProperty("timestamp") val timestamp: Long, // 타임스탬프 (밀리초 단위)
        @JsonProperty("stream_type") val streamType: String, // 스트림 타입 (SNAPSHOT: 스냅샷, REALTIME: 실시간)
        @JsonProperty("market_state_for_ios") val marketStateForIos: String?, // iOS 전용 거래 상태 (Deprecated)
        @JsonProperty("is_trading_suspended") val isTradingSuspended: Boolean?, // 거래 정지 여부 (Deprecated)
    )
