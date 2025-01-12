package com.trading.tradingbot.upbit

import com.trading.tradingbot.upbit.application.command.GetMarketsCommand
import com.trading.tradingbot.upbit.dto.GetOrderbookByMarketsDto
import com.trading.tradingbot.upbit.dto.GetTickerByMarketsDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

/*
* TODO: 에러 처리 추가 필요
* */
@Service
class UpbitService(
    @Value("\${upbit.UPBIT_BASE_URL}")
    private val upbitBaseUrl: String,
) {
    fun getMarkets(): List<GetMarketsCommand>? {
        val uriBuilder = UriComponentsBuilder.fromPath("/v1/market/all")

        val webClient =
            WebClient
                .builder()
                .baseUrl(upbitBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()

        val response =
            webClient
                .get()
                .uri(uriBuilder.build().toUriString())
                .retrieve()
                .bodyToMono<List<GetMarketsCommand>>()
                .timeout(Duration.ofSeconds(100))

        return response.block()
    }

    fun getOrderBook(markets: List<String>): Array<GetOrderbookByMarketsDto>? {
        val uriBuilder = UriComponentsBuilder.fromPath("/v1/orderbook")
        for (market in markets) {
            uriBuilder.queryParam("markets", market)
        }

        val webClient =
            WebClient
                .builder()
                .baseUrl(upbitBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()

        val response =
            webClient
                .get()
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

        val webClient =
            WebClient
                .builder()
                .baseUrl(upbitBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()

        val response =
            webClient
                .get()
                .uri(uriBuilder.build().toUriString())
                .retrieve()
                .bodyToMono<Array<GetTickerByMarketsDto>>()
                .timeout(Duration.ofSeconds(100))

        return response.block()
    }
}
