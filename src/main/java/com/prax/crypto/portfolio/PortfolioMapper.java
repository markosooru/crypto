package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
@Validated
public class PortfolioMapper {

    public PortfolioResponseDto toResponseDto(@Valid Portfolio portfolio, BigDecimal amountEur) {
        return new PortfolioResponseDto(
                portfolio.getId(),
                portfolio.getAmount(),
                portfolio.getCurrency(),
                portfolio.getDateOfPurchase(),
                portfolio.getAppUser().getId(),
                amountEur
        );
    }

    public Portfolio toEntity(@Valid PortfolioDto portfolioDto, @Valid AppUser appUser) {
        return Portfolio.builder()
                .amount(portfolioDto.amount())
                .currency(portfolioDto.currency())
                .dateOfPurchase(portfolioDto.dateOfPurchase())
                .appUser(appUser)
                .build();
    }
}
