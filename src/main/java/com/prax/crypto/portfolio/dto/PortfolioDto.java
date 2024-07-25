package com.prax.crypto.portfolio.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortfolioDto(
        @NotNull
        @Positive
        BigDecimal amount,
        @NotEmpty
        String currency,
        @NotNull
        @PastOrPresent
        LocalDateTime dateOfPurchase
) {
}
