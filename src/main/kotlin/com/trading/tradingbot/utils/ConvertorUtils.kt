package com.trading.tradingbot.utils

object ConvertorUtils {
    fun convertSide(
        side: String,
        type: String,
    ): String =
        when (type) {
            "bithumb" -> convertBithumbSide(side)
            "korbit" -> convertKorbitSide(side)
            else -> throw IllegalArgumentException("Invalid side: $side")
        }

    private fun convertBithumbSide(side: String) =
        when (side) {
            "bid" -> "buy"
            "ask" -> "sell"
            "buy" -> "bid"
            "sell" -> "ask"
            else -> throw IllegalArgumentException("Invalid side: $side")
        }

    private fun convertKorbitSide(side: String) =
        when (side) {
            "buy" -> "buy"
            "sell" -> "sell"
            else -> throw IllegalArgumentException("Invalid side: $side")
        }
}
