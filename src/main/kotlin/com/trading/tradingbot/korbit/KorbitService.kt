package com.trading.tradingbot.korbit

import com.trading.tradingbot.korbit.dto.balances.BalancesResponseDto
import com.trading.tradingbot.korbit.dto.orders.OpenOrdersResponseDto
import com.trading.tradingbot.korbit.dto.orders.OrderResponseDto
import com.trading.tradingbot.trading.TradingServiceInterface
import com.trading.tradingbot.trading.`in`.CreateLimitOrderRequestParams
import com.trading.tradingbot.trading.`in`.CreateMakerOrderRequestParams
import com.trading.tradingbot.trading.out.GetBalancesResponse
import com.trading.tradingbot.trading.out.GetOpenOrdersResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class KorbitService(
    @Value("\${spring.websocket.korbit.apiKey}") private val korbitApiKey: String,
    @Value("\${spring.websocket.korbit.apiSecret}") private val korbitApiSecret: String,
) : TradingServiceInterface {
    override fun createLimitOrder(body: CreateLimitOrderRequestParams) {
        val webClient =
            WebClient
                .builder()
                .baseUrl("https://api.korbit.com/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("X-API-KEY", korbitApiKey)
                .build()

        val timestamp = Instant.now().toEpochMilli().toString()

        val params = mutableMapOf<String, String>()
        params["qty"] = body.qty
        params["price"] = body.price
        params["side"] = body.side
        params["symbol"] = body.symbol
        params["timestamp"] = timestamp

        val query = params.map { "${it.key}=${it.value}" }.joinToString("&")
        val signature = createHmacSignature(query)
        params["signature"] = signature

        try {
            webClient
                .post()
                .uri("/v2/orders")
                .bodyValue("$query&signature=$signature")
                .retrieve()
                .bodyToMono(OrderResponseDto::class.java)
                .block()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    override fun createMakerOrder(body: CreateMakerOrderRequestParams) {
        val webClient =
            WebClient
                .builder()
                .baseUrl("https://api.korbit.com/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("X-API-KEY", korbitApiKey)
                .build()

        val timestamp = Instant.now().toEpochMilli().toString()

        val params = mutableMapOf<String, String>()
        params["side"] = body.side
        params["amt"] = body.amount
        params["recvWindow"] = "5000"
        params["symbol"] = body.symbol
        params["timestamp"] = timestamp

        val query = params.map { "${it.key}=${it.value}" }.joinToString("&")
        val signature = createHmacSignature(query)
        params["signature"] = signature

        try {
            webClient
                .post()
                .uri("/v2/orders")
                .bodyValue("$query&signature=$signature")
                .retrieve()
                .bodyToMono(OrderResponseDto::class.java)
                .block()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    override fun getBalances(currency: String): List<GetBalancesResponse> {
        val webClient =
            WebClient
                .builder()
                .baseUrl("https://api.korbit.com/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("X-API-KEY", korbitApiKey)
                .build()

        val timestamp = Instant.now().toEpochMilli().toString()

        val params = mutableMapOf<String, String>()
        params["currencies"] = currency
        params["timestamp"] = timestamp

        val query = params.map { "${it.key}=${it.value}" }.joinToString("&")
        val signature = createHmacSignature(query)
        params["signature"] = signature

        try {
            return webClient
                .get()
                .uri("/v2/balances?$query&signature=$signature")
                .retrieve()
                .bodyToMono(BalancesResponseDto::class.java)
                .block()
                ?.let { it.data.map { balancesData -> balancesData.toGetBalancesResponse() } } ?: emptyList()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    override fun getOpenOrder(symbol: String): List<GetOpenOrdersResponse> {
        val webClient =
            WebClient
                .builder()
                .baseUrl("https://api.korbit.com/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("X-API-KEY", korbitApiKey)
                .build()

        val timestamp = Instant.now().toEpochMilli().toString()

        val params = mutableMapOf<String, String>()
        params["symbol"] = symbol
        params["timestamp"] = timestamp

        val query = params.map { "${it.key}=${it.value}" }.joinToString("&")
        val signature = createHmacSignature(query)
        params["signature"] = signature

        try {
            return webClient
                .get()
                .uri("/v2/openOrders?$query&signature=$signature")
                .retrieve()
                .bodyToMono(OpenOrdersResponseDto::class.java)
                .block()
                ?.let { it.data.map { orderData -> orderData.toGetOpenOrderResponse() } } ?: emptyList()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    private fun createHmacSignature(query: String): String {
        val secretKeySpec = SecretKeySpec(korbitApiSecret.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)
        val hmacBytes = mac.doFinal(query.toByteArray())
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }
}
