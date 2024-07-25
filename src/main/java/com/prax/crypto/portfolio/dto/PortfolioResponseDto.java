package com.prax.crypto.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortfolioResponseDto(
        Integer id,
        BigDecimal amount,
        String currency,
        LocalDateTime dateOfPurchase,
        Integer appUserId,
        BigDecimal amountEur
) {
}
