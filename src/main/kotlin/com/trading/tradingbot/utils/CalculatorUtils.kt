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

    fun calculatePercentageChange(
        currentPrice: BigDecimal,
        currentQuantity: BigDecimal,
        standard: BigDecimal,
    ): BigDecimal {
        val currentValue = currentPrice.multiply(currentQuantity)

        val difference = currentValue.subtract(standard)
        val percentageChange = difference.divide(standard, 10, RoundingMode.HALF_UP).multiply(BigDecimal(100))
        return percentageChange.setScale(2, RoundingMode.HALF_UP)
    }

    fun convertPriceToTick(price: BigDecimal): BigDecimal {
        val tickerRages =
            listOf(
                BigDecimal("0") to BigDecimal("1") to BigDecimal("0.0001"),
                BigDecimal("1") to BigDecimal("10") to BigDecimal("0.001"),
                BigDecimal("10") to BigDecimal("100") to BigDecimal("0.01"),
                BigDecimal("100") to BigDecimal("1000") to BigDecimal("0.1"),
                BigDecimal("1000") to BigDecimal("5000") to BigDecimal("1"),
                BigDecimal("5000") to BigDecimal("10000") to BigDecimal("5"),
                BigDecimal("10000") to BigDecimal("50000") to BigDecimal("10"),
                BigDecimal("50000") to BigDecimal("100000") to BigDecimal("50"),
                BigDecimal("100000") to BigDecimal("500000") to BigDecimal("100"),
                BigDecimal("500000") to BigDecimal("1000000") to BigDecimal("500"),
                BigDecimal("1000000") to BigDecimal("999999999999") to BigDecimal("1000"),
            )

        // 100만원 이상은 1000원 단위로 처리
        val tickSize =
            tickerRages
                .firstOrNull { (range, _) ->
                    price >= range.first && price <= range.second
                }?.second ?: BigDecimal("1000")

        return price.divide(tickSize, 0, RoundingMode.HALF_UP).multiply(tickSize)
    }
}
