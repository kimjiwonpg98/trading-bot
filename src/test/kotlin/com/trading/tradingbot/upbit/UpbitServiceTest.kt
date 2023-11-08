package com.trading.tradingbot.upbit

import com.trading.tradingbot.common.ConfigService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest

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
}
