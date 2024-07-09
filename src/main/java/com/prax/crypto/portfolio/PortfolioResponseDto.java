package com.prax.crypto.portfolio;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortfolioResponseDto(
        @NotNull
        Integer id,
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        String currency,
        @NotNull
        @PastOrPresent
        LocalDateTime dateOfPurchase,
        @NotNull
        Integer appUserId,
        BigDecimal amountEur
) {
}
