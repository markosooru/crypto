package com.prax.crypto.account;

import com.prax.crypto.portfolio.Portfolio;

import java.util.List;

public record AppUserResponseDto(
        Integer Id,
        String email,
        List<Portfolio> portfolioItems
) {
}
