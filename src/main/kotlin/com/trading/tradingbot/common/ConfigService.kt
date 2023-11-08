package com.trading.tradingbot.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ConfigService (@Value("\${upbit.UPBIT_BASE_URL}") private val upbitBaseUrl: String) {
        @Bean
        fun getUpbitBaseUrl (): String {
                return upbitBaseUrl
        }
}