package com.trading.tradingbot.utils

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class CalculatorUtils {
    fun getBuyPrice(
        currentPrice: BigDecimal,
        gridPercent: BigDecimal,
        margin: Int,
    ): BigDecimal = currentPrice.subtract(BigDecimal.ONE.subtract(gridPercent * BigDecimal(margin)))

    fun getSellPrice(
        currentPrice: BigDecimal,
        gridPercent: BigDecimal,
        margin: Int,
    ): BigDecimal = currentPrice.subtract(BigDecimal.ONE.add(gridPercent * BigDecimal(margin)))

    fun getCurrentKrwValueByAmount(
        currencyPrice: BigDecimal,
        amount: BigDecimal,
    ): BigDecimal = currencyPrice.multiply(amount)

    fun calculateOrderAmount(
        initialInvestment: BigDecimal, // 최초 자금
        availableFunds: BigDecimal, // 지금 가지고 있는 자금
        gridCount: Int, // 그리드 봇 개수
        price: BigDecimal, // 가격
    ): BigDecimal {
        val gridInvestment = initialInvestment.divide(BigDecimal(gridCount), 8, RoundingMode.HALF_UP)
        val maxAmount = gridInvestment.divide(price, 8, RoundingMode.HALF_UP)
        val availableAmount = availableFunds.divide(price, 8, RoundingMode.HALF_UP)
        return minOf(maxAmount, availableAmount)
    }
}
