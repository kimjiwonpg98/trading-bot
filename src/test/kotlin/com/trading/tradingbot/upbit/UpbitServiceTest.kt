package com.trading.tradingbot.upbit

import com.trading.tradingbot.common.ConfigService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.junit.jupiter.api.Assertions.assertThrows

/**
 * TODO: 비효율적인 hasProperty 검사 최적화 필요
 */
@RestClientTest(ConfigService::class, UpbitService::class)
class UpbitServiceTest () {
    private lateinit var configService: ConfigService
    private lateinit var upbitService: UpbitService

    @BeforeEach
    fun setup() {
        configService = ConfigService("https://api.upbit.com")
        upbitService = UpbitService(configService)
    }

    @DisplayName("getMarkets")
    @Test
    fun getMarketsTest() {
        upbitService.getMarkets()
            ?.apply {
            for (coin in this) {
                assertThat(coin, hasProperty("market"))
                assertThat(coin, hasProperty("korean_name"))
                assertThat(coin, hasProperty("english_name"))
            }}
    }

    @DisplayName("getOrderBook")
    @Test
    fun getOrderBook() {
        upbitService.getOrderBook(markets = listOf("KRW-BTC", "KRW-ETH"))
            ?.apply {
                for (orderbook in this) {
                    assertThat(orderbook, hasProperty("market"))
                    assertThat(orderbook, hasProperty("timestamp"))
                    assertThat(orderbook, hasProperty("total_ask_size"))
                    assertThat(orderbook, hasProperty("total_bid_size"))
                    assertThat(orderbook, hasProperty("orderbook_units"))
                }}
    }

    @DisplayName("getOrderBook")
    @Test
    fun getOrderbookFailureCase() {
        val exception = assertThrows(WebClientResponseException.NotFound::class.java) {
            upbitService.getOrderBook(markets = listOf("KRW-BTCD", "KRW-ETH"))
        }

        assert(exception.statusCode.is4xxClientError)
    }

    @DisplayName("getTickers")
    @Test
    fun getTickers() {
        upbitService.getTickers(markets = listOf("KRW-BTC", "KRW-ETH"))
            ?.apply {
                for (ticker in this) {
                    assertThat(ticker, hasProperty("market"))
                    assertThat(ticker, hasProperty("trade_date"))
                    assertThat(ticker, hasProperty("trade_time"))
                    assertThat(ticker, hasProperty("opening_price"))
                    assertThat(ticker, hasProperty("high_price"))
                    assertThat(ticker, hasProperty("low_price"))
                    assertThat(ticker, hasProperty("trade_price"))
                    assertThat(ticker, hasProperty("prev_closing_price"))
                }}
    }

    @DisplayName("getTickers")
    @Test
    fun getTickersFailureCase() {
        val exception = assertThrows(WebClientResponseException.NotFound::class.java) {
            upbitService.getTickers(markets = listOf("KRW-BTCD", "KRW-ETH"))
        }

        assert(exception.statusCode.is4xxClientError)
    }
}
