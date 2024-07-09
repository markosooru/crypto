package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioMapperTest {

    @InjectMocks
    private PortfolioMapper portfolioMapper;

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    void toResponseDto_givenPortfolioEntity_mapsEntityToResponseDto() {
        // given
        var portfolio = new Portfolio(
                1,
                new BigDecimal("1.1234"),
                "BTC",
                LocalDateTime.of(2023, 6, 28, 14, 30, 0),
                false,
                new AppUser()
        );
        var amountEur = new BigDecimal("1000.123");

        // when
        var dto = portfolioMapper.toResponseDto(portfolio, amountEur);

        // then
        assertEquals(portfolio.getAmount(), dto.amount());
        assertEquals(portfolio.getCurrency(), dto.currency());
        assertEquals(portfolio.getDateOfPurchase(), dto.dateOfPurchase());
        assertEquals(portfolio.getAppUser().getId(), dto.appUserId());
        assertEquals(amountEur, dto.amountEur());
    }

    @Test
    void toEntity_givenPortfolioDto_mapsDtoToEntity() {
        // given
        var portfolioDto = new PortfolioDto(
                new BigDecimal("1.1234"),
                "BTC",
                LocalDateTime.of(2023, 6, 28, 14, 30, 0),
                1
        );
        var appUser = AppUser.builder().id(1).build();

        // mock
        when(appUserRepository.findById(anyInt())).thenReturn(Optional.ofNullable(appUser));

        // when
        var result = portfolioMapper.toEntity(portfolioDto);

        // then
        assertEquals(portfolioDto.amount(), result.getAmount());
        assertEquals(portfolioDto.currency(), result.getCurrency());
        assertEquals(portfolioDto.dateOfPurchase(), result.getDateOfPurchase());
        assertEquals(portfolioDto.appUserId(), result.getAppUser().getId());
    }
}