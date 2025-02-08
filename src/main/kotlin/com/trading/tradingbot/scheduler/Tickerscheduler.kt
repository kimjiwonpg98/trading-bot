package com.trading.tradingbot.scheduler

import com.trading.tradingbot.bithumb.BithumbService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Tickerscheduler(
    private val bithumbService: BithumbService,
) {
    @Scheduled(cron = "5 * * * * *")
    fun getBithumbTicker() {
        bithumbService.getTicker(symbol = "BTC")
    }
}
