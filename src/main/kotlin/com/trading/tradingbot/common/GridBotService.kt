package com.trading.tradingbot.common

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GridBotService(
    private val client: ExchangeService,
) {
    fun createGridStrategy(config: GridBotConfig): List<GridOrder> {
        val gridWidth = (config.gridUpperBound - config.gridLowerBound) / config.gridCount
        val gridOrders = mutableListOf<GridOrder>()

        for (i in 0 until config.gridCount.toInt()) {
            val gridPrice = config.gridLowerBound + (gridWidth * BigDecimal(i))
            val orderAmount = config.investmentAmount / (config.gridCount * gridPrice)

            gridOrders.add(
                GridOrder(
                    type = OrderType.BUY,
                    price = gridPrice,
                    amount = orderAmount,
                ),
            )
            gridOrders.add(
                GridOrder(
                    type = OrderType.SELL,
                    price = gridPrice + gridWidth,
                    amount = orderAmount,
                ),
            )
        }

        return gridOrders
    }

    fun executeGridStrategy(config: GridBotConfig) {
        val currentPrice = client.getCurrentPrice(config.symbol)
        val gridOrders = createGridStrategy(config)

        gridOrders.forEach { order ->
            when (order.type) {
                OrderType.BUY -> {
                    if (currentPrice <= order.price) {
                        val executedOrder = client.createLimitBuyOrder(config.symbol, order.amount, order.price)
//                        orderRepository.save(executedOrder)
                    }
                }
                OrderType.SELL -> {
                    if (currentPrice >= order.price) {
                        val executedOrder = client.createLimitSellOrder(config.symbol, order.amount, order.price)
//                        orderRepository.save(executedOrder)
                    }
                }
            }
        }
    }
}
