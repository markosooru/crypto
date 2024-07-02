package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PortfolioMapper {

    private final AppUserRepository appUserRepository;

    public PortfolioMapper(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public PortfolioResponseDto toResponseDto(Portfolio portfolio, BigDecimal amountEur) {
        return new PortfolioResponseDto(
                portfolio.getAmount(),
                portfolio.getCurrency(),
                portfolio.getDateOfPurchase(),
                portfolio.getAppUser().getId(),
                amountEur
        );
    }

    public Portfolio toEntity(PortfolioDto portfolioDto) {
        var existingUser = appUserRepository.findById(portfolioDto.appUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Portfolio.builder()
                .amount(portfolioDto.amount())
                .currency(portfolioDto.currency())
                .dateOfPurchase(portfolioDto.dateOfPurchase())
                .appUser(existingUser)
                .build();
    }
}
