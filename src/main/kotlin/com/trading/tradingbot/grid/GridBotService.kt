package com.trading.tradingbot.grid

import com.trading.tradingbot.trading.TradingServiceInterface
import com.trading.tradingbot.trading.`in`.CreateLimitOrderRequestParams
import com.trading.tradingbot.trading.`in`.CreateMakerOrderRequestParams
import com.trading.tradingbot.utils.CalculatorUtils
import com.trading.tradingbot.websocket.korbit.event.KorbitWebSocketTickerEvent
import com.trading.tradingbot.websocket.upbit.event.UpbitWebSocketTickerEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GridBotService(
    private val calculatorUtils: CalculatorUtils,
    @Qualifier("korbitService")
    private val korbitService: TradingServiceInterface,
    @Qualifier("bithumbService")
    private val bithumbService: TradingServiceInterface,
) {
    private val tradingPair = "BTC-KRW"

    // 초기 투자할 돈
    private val initialInvestment = BigDecimal(1000000)

    // 그리드 봇 개수
    private val gridCount = 2

    // 그리드 퍼센트
    private val gridPercentage = 0.01

    // 타켓
    private val targetValue = BigDecimal(1000000)

    // 투자 초기
    private var availableFunds = initialInvestment
    private var lastPrice = BigDecimal.ZERO
    private var currentHolding = BigDecimal.ZERO
    private val standardPercent = BigDecimal(2)

    @EventListener(UpbitWebSocketTickerEvent::class)
    fun upbitStrategy(event: UpbitWebSocketTickerEvent) {
        val currentPrice = event.ticketEvent.tradePrice
        commonStrategy(currentPrice, BigDecimal.ZERO)
    }

    @EventListener(KorbitWebSocketTickerEvent::class)
    fun korbit(event: KorbitWebSocketTickerEvent) {
        val currentPrice = event.ticketEvent.close
        val balances = korbitService.getBalances("btc")
        val currencyBalance = balances.filter { it.currency == "btc" }
        val qty = currencyBalance.map { it.available }[0]
        val availableQty = BigDecimal(qty).setScale(7, RoundingMode.DOWN).stripTrailingZeros()

        if (availableQty == BigDecimal.ZERO) {
            val params =
                CreateMakerOrderRequestParams(
                    targetValue.stripTrailingZeros().toPlainString(),
                    "btc_krw",
                    "buy",
                )
            korbitService.createMakerOrder(params)
        }

        val gridOrder = commonStrategy(currentPrice, BigDecimal(qty))
        val openOrder = korbitService.getOpenOrder("btc_krw")

        val side =
            gridOrder
                .getOrNull(0)
                ?.type
                .toString()
                .lowercase()

        val openOrderSize = openOrder.filter { it.side == side }.size
        val orderSize = maxOf(4 - openOrderSize, 0)

        gridOrder.take(orderSize).map {
            val params =
                CreateLimitOrderRequestParams(
                    it.amount
                        .setScale(8, RoundingMode.DOWN)
                        .stripTrailingZeros()
                        .toPlainString(),
                    "btc_krw",
                    it.type.name.lowercase(),
                    it.price.stripTrailingZeros().toPlainString(),
                )

            korbitService.createLimitOrder(params)
        }
    }

    fun commonStrategy(
        currentPrice: BigDecimal,
        qty: BigDecimal,
    ): List<GridOrder> {
        if (lastPrice == BigDecimal.ZERO) {
            lastPrice = currentPrice
            println("Initial price: $currentPrice")
            return emptyList()
        }

        val currentValue = currentHolding.multiply(currentPrice)
        val valueDifference = targetValue.subtract(currentValue)

        val orderAmount =
            calculatorUtils.calculateOrderAmount(
                initialInvestment,
                availableFunds,
                gridCount,
                currentPrice,
            )

        val percent =
            calculatorUtils.calculatePercentageChange(
                currentPrice = currentPrice,
                currentQuantity = qty,
                standard = targetValue,
            )

        return createInfinityGridStrategy(
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
            percent = percent,
            standardPercent = standardPercent,
        )
    }

    fun createInfinityGridStrategy(
        config: InfinityGridBotConfig,
        orderAmount: BigDecimal,
        currentPrice: BigDecimal,
        valueDifference: BigDecimal,
        percent: BigDecimal,
        standardPercent: BigDecimal,
    ): List<GridOrder> {
        val gridOrders = mutableListOf<GridOrder>()

        for (i in 0 until config.gridCount) {
            if (percent <= -standardPercent) {
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

            if (percent >= standardPercent) {
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
