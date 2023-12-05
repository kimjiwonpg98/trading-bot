package com.trading.tradingbot.upbit.application.command

import com.fasterxml.jackson.annotation.JsonProperty

data class GetMarketsCommand(
    val market: String,
    @JsonProperty("korean_name")
    val koreanName: String,
    @JsonProperty("english_name")
    val englishName: String
)
