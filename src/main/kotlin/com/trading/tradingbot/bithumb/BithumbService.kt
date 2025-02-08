package com.trading.tradingbot.bithumb

import com.trading.tradingbot.bithumb.dto.GetBalancesByBithumbDto
import com.trading.tradingbot.bithumb.dto.GetOpenOrdersByBithumbDto
import com.trading.tradingbot.bithumb.dto.GetTickerByBithumbDto
import com.trading.tradingbot.bithumb.event.BitumbTickerEvent
import com.trading.tradingbot.trading.TradingService
import com.trading.tradingbot.trading.`in`.CreateLimitOrderRequestParams
import com.trading.tradingbot.trading.`in`.CreateMakerOrderRequestParams
import com.trading.tradingbot.trading.out.GetBalancesResponse
import com.trading.tradingbot.trading.out.GetOpenOrdersResponse
import com.trading.tradingbot.utils.ConvertorUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.UUID
import javax.crypto.SecretKey

@Service
class BithumbService(
    @Value("\${spring.websocket.bithumb.apiKey}") private val bithumbApiKey: String,
    @Value("\${spring.websocket.bithumb.apiSecret}") private val bithumbApiSecret: String,
    private val eventPublisher: ApplicationEventPublisher,
) : TradingService {
    override fun createLimitOrder(params: CreateLimitOrderRequestParams) {
        try {
            val body =
                mapOf(
                    "market" to "KRW-${params.symbol.uppercase()}",
                    "side" to ConvertorUtils.convertSide(params.side, "bithumb"),
                    "volume" to params.qty,
                    "price" to params.price,
                    "ord_type" to "limit",
                )

            defaultWebClient()
                .post()
                .uri("/v1/orders")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void::class.java)
                .block()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    override fun createMakerOrder(params: CreateMakerOrderRequestParams) {
        try {
            val body =
                mapOf(
                    "market" to "KRW-${params.symbol.uppercase()}",
                    "side" to ConvertorUtils.convertSide(params.side, "bithumb"),
                    "volume" to params.amount,
                    "ord_type" to "market",
                )

            defaultWebClient()
                .post()
                .uri("/v1/orders")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void::class.java)
                .block()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    override fun getBalances(currency: String): List<GetBalancesResponse> {
        try {
            return defaultWebClient()
                .get()
                .uri("/v1/accounts")
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<GetBalancesByBithumbDto>>() {})
                .block()
                ?.map { it.toGetBalancesResponse() } ?: emptyList()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    override fun getOpenOrder(symbol: String): List<GetOpenOrdersResponse> {
        val params = mutableMapOf<String, String>()
        params["market"] = "KRW-${symbol.uppercase()}"
        params["state"] = "wait"

        val query = params.map { "${it.key}=${it.value}" }.joinToString("&")

        try {
            return defaultWebClient()
                .get()
                .uri("/v1/orders?$query")
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<GetOpenOrdersByBithumbDto>>() {})
                .block()
                ?.map { it.toGetOpenOrdersResponse() } ?: emptyList()
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    fun getTicker(symbol: String) {
        try {
            val ticker =
                defaultWebClient()
                    .get()
                    .uri("/v1/ticker?markets=KRW-${symbol.uppercase()}")
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<List<GetTickerByBithumbDto>>() {})
                    .block()
                    ?.map { it.toGetTickerResponse() }
                    ?.first()!!
            println(ticker)
            eventPublisher.publishEvent((BitumbTickerEvent(ticker)))
        } catch (e: WebClientResponseException) {
            throw e
        }
    }

    private fun createJwt(): String {
        val secret: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(bithumbApiSecret))
        val timestamp = System.currentTimeMillis()
        val claims = mapOf("access_key" to bithumbApiKey, "nonce" to UUID.randomUUID().toString(), "timestamp" to timestamp)

        val jwtToken: String =
            Jwts
                .builder()
                .claims(claims)
                .signWith(secret, Jwts.SIG.HS256)
                .compact()
        return "Bearer $jwtToken"
    }

    private fun defaultWebClient(): WebClient =
        WebClient
            .builder()
            .baseUrl("https://api.bithumb.com/")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", createJwt())
            .build()
}
