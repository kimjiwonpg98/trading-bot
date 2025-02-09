package com.trading.tradingbot.grid

import com.trading.tradingbot.bithumb.event.BitumbTickerEvent
import com.trading.tradingbot.trading.TradingService
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
    private val korbitService: TradingService,
    @Qualifier("bithumbService")
    private val bithumbService: TradingService,
) {
    @EventListener(UpbitWebSocketTickerEvent::class)
    fun upbitStrategy(event: UpbitWebSocketTickerEvent) {
        val currentPrice = event.ticketEvent.tradePrice
//        commonStrategy(currentPrice, BigDecimal.ZERO)
    }

    @EventListener(BitumbTickerEvent::class)
    fun bitumb(event: BitumbTickerEvent) {
        val config =
            InfinityGridBotConfig(
                symbol = "DOGE",
                currency = "DOGE",
                initialAmount = BigDecimal(0.0001),
                gridLowerBound = BigDecimal(100),
                gridCount = 4,
                gridPercent = BigDecimal(0.01),
                initialInvestment = BigDecimal(1000000),
            )

        val currentPrice = event.ticketEvent.close
        val balances = bithumbService.getBalances(config.currency)
        val currencyBalance = balances.filter { it.currency == config.currency }
        val qty = currencyBalance.map { it.available }[0]
        val avgPrice = currencyBalance.map { it.avgPrice }[0]
        val availableQty = BigDecimal(qty).setScale(7, RoundingMode.DOWN).stripTrailingZeros()

        if (availableQty == BigDecimal.ZERO) {
            val params =
                CreateMakerOrderRequestParams(
                    config.initialAmount.stripTrailingZeros().toPlainString(),
                    config.symbol,
                    OrderType.BUY.name.lowercase(),
                )
            bithumbService.createMakerOrder(params)
        }

        val gridOrder = commonStrategy(currentPrice, BigDecimal(avgPrice), BigDecimal(qty), config)
        val openOrder = bithumbService.getOpenOrder(config.symbol)

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
                    config.symbol,
                    it.type.name.lowercase(),
                    it.price.stripTrailingZeros().toPlainString(),
                )

            bithumbService.createLimitOrder(params)
        }
    }

    @EventListener(KorbitWebSocketTickerEvent::class)
    fun korbit(event: KorbitWebSocketTickerEvent) {
        val config =
            InfinityGridBotConfig(
                symbol = "btc_krw",
                currency = "btc",
                initialAmount = BigDecimal(0.0001),
                gridLowerBound = BigDecimal(100),
                gridCount = 4,
                gridPercent = BigDecimal(0.01),
                initialInvestment = BigDecimal(1000000),
            )

        val currentPrice = event.ticketEvent.close
        val balances = korbitService.getBalances(config.currency)
        val currencyBalance = balances.filter { it.currency == config.currency }
        val qty = currencyBalance.map { it.available }[0]
        val avgPrice = currencyBalance.map { it.avgPrice }[0]
        val availableQty = BigDecimal(qty).setScale(7, RoundingMode.DOWN).stripTrailingZeros()

        if (availableQty == BigDecimal.ZERO) {
            val params =
                CreateMakerOrderRequestParams(
                    config.initialAmount.stripTrailingZeros().toPlainString(),
                    config.symbol,
                    OrderType.BUY.name.lowercase(),
                )
            korbitService.createMakerOrder(params)
        }

        val gridOrder = commonStrategy(currentPrice, BigDecimal(avgPrice), BigDecimal(qty), config)
        val openOrder = korbitService.getOpenOrder(config.symbol)

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
        avgPrice: BigDecimal,
        qty: BigDecimal,
        config: InfinityGridBotConfig,
    ): List<GridOrder> {
        if (currentPrice == BigDecimal.ZERO) {
            println("Initial price: $currentPrice")
            return emptyList()
        }

        val orderAmount =
            calculatorUtils.calculateOrderAmount(
                config.initialInvestment,
                config.gridCount,
                currentPrice,
            )

        val percent =
            calculatorUtils.calculatePercentageChange(
                currentPrice = currentPrice,
                standard = avgPrice,
            )

        return createInfinityGridStrategy(
            config = config,
            orderAmount = orderAmount,
            currentPrice = currentPrice,
            percent = percent,
            standardPercent = config.gridPercent,
        )
    }

    fun createInfinityGridStrategy(
        config: InfinityGridBotConfig,
        orderAmount: BigDecimal,
        currentPrice: BigDecimal,
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
