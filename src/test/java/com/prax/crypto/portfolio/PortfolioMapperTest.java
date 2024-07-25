package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.portfolio.dto.PortfolioDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PortfolioMapperTest {

    @InjectMocks
    private PortfolioMapper portfolioMapper;

    @Test
    void toResponseDto_givenPortfolioEntity_mapsEntityToResponseDto() {
        // given
        var amountEur = new BigDecimal("12345.67");
        var portfolio = new Portfolio(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                new AppUser()
        );

        // when
        var dto = portfolioMapper.toResponseDto(portfolio, amountEur);

        // then
        assertEquals(portfolio.getId(), dto.id());
        assertEquals(portfolio.getAmount(), dto.amount());
        assertEquals(portfolio.getCurrency(), dto.currency());
        assertEquals(portfolio.getDateOfPurchase(), dto.dateOfPurchase());
        assertEquals(portfolio.getAppUser().getId(), dto.appUserId());
        assertEquals(amountEur, dto.amountEur());
    }

    @Test
    void toEntity_givenPortfolioDto_mapsDtoToEntity() {
        // given
        var appUser = new AppUser();
        var portfolioDto = new PortfolioDto(
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1)
        );

        // when
        var result = portfolioMapper.toEntity(portfolioDto, appUser);

        // then
        assertEquals(portfolioDto.amount(), result.getAmount());
        assertEquals(portfolioDto.currency(), result.getCurrency());
        assertEquals(portfolioDto.dateOfPurchase(), result.getDateOfPurchase());
    }
}