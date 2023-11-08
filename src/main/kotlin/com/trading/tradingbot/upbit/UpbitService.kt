package com.trading.tradingbot.upbit

import com.trading.tradingbot.common.ConfigService
import com.trading.tradingbot.upbit.dto.GetMarketsDto
import com.trading.tradingbot.upbit.dto.GetOrderbookByMarketsDto
import com.trading.tradingbot.upbit.dto.GetTickerByMarketsDto
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration
import org.springframework.web.util.UriComponentsBuilder


/*
* TODO: 에러 처리 추가 필요
* */
@Service
class UpbitService (private val configService: ConfigService) {
    private val upbitBaseUrl = this.configService.getUpbitBaseUrl()

    fun getMarkets(): Array<GetMarketsDto>? {
        val uriBuilder = UriComponentsBuilder.fromPath("/v1/market/all")

        val webClient = WebClient.builder().baseUrl(upbitBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val response = webClient.get().uri(uriBuilder.build().toUriString())
            .retrieve()
            .bodyToMono<Array<GetMarketsDto>>().timeout(Duration.ofSeconds(100))

        return response.block()
    }

    fun getOrderBook(markets: List<String>): Array<GetOrderbookByMarketsDto>? {
        val uriBuilder = UriComponentsBuilder.fromPath("/v1/orderbook")
        for (market in markets) {
            uriBuilder.queryParam("markets", market)
        }

        val webClient = WebClient.builder().baseUrl(upbitBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val response = webClient.get()
            .uri(uriBuilder.build().toUriString())
            .retrieve()
            .bodyToMono<Array<GetOrderbookByMarketsDto>>()
            .timeout(Duration.ofSeconds(100))

        return response.block()
    }

    fun getTickers(markets: List<String>): Array<GetTickerByMarketsDto>? {
        val uriBuilder = UriComponentsBuilder.fromPath("/v1/ticker")
        for (market in markets) {
            uriBuilder.queryParam("markets", market)
        }

        val webClient = WebClient.builder().baseUrl(upbitBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val response = webClient.get()
            .uri(uriBuilder.build().toUriString())
            .retrieve()
            .bodyToMono<Array<GetTickerByMarketsDto>>()
            .timeout(Duration.ofSeconds(100))

        return response.block()
    }
}