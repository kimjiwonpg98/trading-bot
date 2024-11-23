package com.trading.tradingbot.common

import com.trading.tradingbot.utils.CalculatorUtils
import com.trading.tradingbot.websocket.korbit.event.KorbitWebSocketTickerEvent
import com.trading.tradingbot.websocket.upbit.event.UpbitWebSocketTickerEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GridBotService(
    private val calculatorUtils: CalculatorUtils,
) {
    private val tradingPair = "BTC-KRW"
    private val initialInvestment = BigDecimal(1000000)
    private val gridCount = 5
    private val gridPercentage = 0.01
    private val targetValue = BigDecimal(1000000)
    private var availableFunds = initialInvestment
    private var lastPrice = BigDecimal.ZERO
    private var currentHolding = BigDecimal.ZERO

    @EventListener(UpbitWebSocketTickerEvent::class)
    fun upbitStrategy(event: UpbitWebSocketTickerEvent) {
        val currentPrice = event.ticketEvent.tradePrice
        commonStrategy(currentPrice)
    }

    @EventListener(KorbitWebSocketTickerEvent::class)
    fun korbit(event: KorbitWebSocketTickerEvent) {
        val currentPrice = event.ticketEvent.close
        commonStrategy(currentPrice)
    }

    fun commonStrategy(currentPrice: BigDecimal) {
        if (lastPrice == BigDecimal.ZERO) {
            lastPrice = currentPrice
            println("Initial price: $currentPrice")
            return
        }

        val currentValue = currentHolding.multiply(currentPrice)
        val valueDifference = targetValue.subtract(currentValue)

        val priceChange = (currentPrice - lastPrice) / lastPrice
        val orderAmount =
            calculatorUtils.calculateOrderAmount(
                initialInvestment,
                availableFunds,
                gridCount,
                currentPrice,
            )

        createInfinityGridStrategy(
            config =
                InfinityGridBotConfig(
                    symbol = tradingPair,
                    initialPrice = BigDecimal(1000000000),
                    gridLowerBound = BigDecimal(100),
                    gridCount = 10,
                    gridPercent = BigDecimal(gridPercentage),
                ),
            orderAmount = orderAmount,
            currentPrice = currentPrice,
            valueDifference = valueDifference,
        )
    }

    fun createInfinityGridStrategy(
        config: InfinityGridBotConfig,
        orderAmount: BigDecimal,
        currentPrice: BigDecimal,
        valueDifference: BigDecimal,
    ): List<GridOrder> {
        val gridOrders = mutableListOf<GridOrder>()

        for (i in 0 until config.gridCount) {
            if (valueDifference > BigDecimal.ZERO) {
                val buyPrice = calculatorUtils.getBuyPrice(currentPrice, config.gridPercent, i)
                if (buyPrice >= config.gridLowerBound) {
                    gridOrders.add(
                        GridOrder(
                            type = OrderType.BUY,
                            price = buyPrice,
                            amount = orderAmount,
                        ),
                    )
                }
            }

            if (valueDifference < BigDecimal.ZERO) {
                val sellPrice = calculatorUtils.getSellPrice(currentPrice, config.gridPercent, i)
                gridOrders.add(
                    GridOrder(
                        type = OrderType.SELL,
                        price = sellPrice,
                        amount = orderAmount,
                    ),
                )
            }
        }

        return gridOrders
    }

    companion object {
//        const val logger  = KotlinLogging.logger {}
    }
}
