package com.prax.crypto.bitfinex;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Ticker(
    BigDecimal bid,
    BigDecimal bidSize,
    BigDecimal ask,
    BigDecimal askSize,
    BigDecimal dailyChange,
    BigDecimal dailyChangeRelative,
    BigDecimal lastPrice,
    BigDecimal volume,
    BigDecimal high,
    BigDecimal low
) {
}
