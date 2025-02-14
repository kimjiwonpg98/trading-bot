package com.trading.tradingbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class TradingBotApplication

fun main(args: Array<String>) {
    runApplication<TradingBotApplication>(*args)
}
