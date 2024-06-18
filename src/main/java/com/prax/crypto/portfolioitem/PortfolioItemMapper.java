package com.prax.crypto.portfolioitem;

import com.prax.crypto.appuser.AppUserRepository;
import com.prax.crypto.bitfinex.BitfinexService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PortfolioItemMapper {

    private final AppUserRepository appUserRepository;
    private final BitfinexService bitfinexService;

    public PortfolioItemMapper(AppUserRepository appUserRepository, BitfinexService bitfinexService) {
        this.appUserRepository = appUserRepository;
        this.bitfinexService = bitfinexService;
    }

    public PortfolioItemResponseDto toResponseDto(PortfolioItem portfolioItem) {
        BigDecimal currentPriceInEUR = this.bitfinexService.getCurrentPriceInEUR(portfolioItem.getCurrency());
        BigDecimal amountEur = currentPriceInEUR.multiply(portfolioItem.getAmount());

        return new PortfolioItemResponseDto(
                portfolioItem.getAmount(),
                portfolioItem.getCurrency(),
                portfolioItem.getDateOfPurchase(),
                portfolioItem.getAppUser().getId(),
                amountEur
        );
    }

    public PortfolioItem toEntity(PortfolioItemDto portfolioItemDto) {
        var existingUser = this.appUserRepository.findById(portfolioItemDto.appUserId()).orElse(null);
        if (existingUser == null) {
            return null;
        }

        return new PortfolioItem(
                portfolioItemDto.amount(),
                portfolioItemDto.currency(),
                portfolioItemDto.dateOfPurchase(),
                existingUser
        );
    }
}
