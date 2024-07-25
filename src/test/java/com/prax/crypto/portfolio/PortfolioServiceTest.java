package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserService;
import com.prax.crypto.bitfinex.BitfinexService;
import com.prax.crypto.portfolio.dto.PortfolioDto;
import com.prax.crypto.portfolio.dto.PortfolioResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioMapper portfolioMapper;

    @Mock
    private BitfinexService bitfinexService;

    @Mock
    private AppUserService appUserService;

    @Test
    void create_givenPortfolioDto_savesAndReturnsResponseDto() {
        // given
        var appUser = new AppUser();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioDto = new PortfolioDto(
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1)
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("12345.67")
        );

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(bitfinexService.getCryptoFxInEur(portfolioDto.currency())).thenReturn(new BigDecimal("100000.00"));
        when(portfolioMapper.toEntity(portfolioDto, appUser)).thenReturn(portfolio);
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);
        when(portfolioMapper.toResponseDto(eq(portfolio), any(BigDecimal.class))).thenReturn(portfolioResponseDto);

        // when
        var result = portfolioService.create(portfolioDto);

        // then
        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void findAll_executes_returnsListOfActivePortfolioDtos() {
        // given
        var appUser = new AppUser();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("12345.67")
        );

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(bitfinexService.getCryptoFxInEur(portfolio.getCurrency())).thenReturn(new BigDecimal("100000.00"));
        when(portfolioMapper.toResponseDto(eq(portfolio), any(BigDecimal.class))).thenReturn(portfolioResponseDto);
        when(portfolioRepository.findAllActiveByAppUser(appUser)).thenReturn(List.of(portfolio));

        // when
        var result = portfolioService.findAll();

        // then
        assertEquals(1, result.size());
        assertEquals(portfolioResponseDto, result.getFirst());
        verify(portfolioRepository).findAllActiveByAppUser(appUser);
        verify(appUserService).getAuthenticatedUser();
        verify(bitfinexService).getCryptoFxInEur(portfolio.getCurrency());
    }

    @Test
    void findById_givenId_returnsCorrespondingResponseDtoIfActive() {
        // given
        var appUser = new AppUser();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("12345.67")
        );

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(portfolioRepository.findActiveByIdAndAppUser(1, appUser)).thenReturn(Optional.of(portfolio));
        when(bitfinexService.getCryptoFxInEur(portfolio.getCurrency())).thenReturn(new BigDecimal("100000.00"));
        when(portfolioMapper.toResponseDto(eq(portfolio), any(BigDecimal.class))).thenReturn(portfolioResponseDto);

        // when
        var result = portfolioService.findById(1);

        // then
        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).findActiveByIdAndAppUser(1, appUser);
    }

    @Test
    void findById_givenWrongId_throwsEntityNotFoundException() {
        // given
        var appUser = new AppUser();

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(portfolioRepository.findActiveByIdAndAppUser(eq(1), eq(appUser)))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> portfolioService.findById(1));
        verify(portfolioRepository).findActiveByIdAndAppUser(eq(1), eq(appUser));
    }

    @Test
    void update_givenIdAndPortfolioDto_savesAndReturnsResponseDto() {
        // given
        var appUser = new AppUser();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("0.87654321"),
                "ETH",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioDto = new PortfolioDto(
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1)
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("12345.67")
        );

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(bitfinexService.getCryptoFxInEur(portfolioDto.currency())).thenReturn(new BigDecimal("100000.00"));
        when(portfolioRepository.findActiveByIdAndAppUser(1, appUser)).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class))).thenReturn(portfolioResponseDto);

        // when
        var result = portfolioService.update(1, portfolioDto);

        // then
        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).findActiveByIdAndAppUser(1, appUser);
        verify(appUserService).getAuthenticatedUser();
        verify(bitfinexService).getCryptoFxInEur(portfolioDto.currency());
        verify(portfolioRepository).save(portfolio);
    }

    @Test
    void update_givenWrongId_throwsEntityNotFoundException() {
        // given
        var appUser = new AppUser();
        var portfolioDto = new PortfolioDto(
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1)
        );

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(bitfinexService.getCryptoFxInEur(portfolioDto.currency())).thenReturn(new BigDecimal("100000.00"));
        when(portfolioRepository.findActiveByIdAndAppUser(1, appUser)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> portfolioService.update(1, portfolioDto));
        verify(portfolioRepository).findActiveByIdAndAppUser(1, appUser);
    }

    @Test
    void delete_givenId_setsDeletedFlagToTrue() {
        // given
        var appUser = new AppUser();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(portfolioRepository.findActiveByIdAndAppUser(1, appUser)).thenReturn(Optional.of(portfolio));

        // when
        portfolioService.delete(1);

        // then
        assertTrue(portfolio.isDeleted());
        verify(portfolioRepository).findActiveByIdAndAppUser(1, appUser);
        verify(portfolioRepository).save(portfolio);
    }

    @Test
    void delete_givenWrongId_throwsEntityNotFoundException() {
        // given
        var appUser = new AppUser();

        // mock
        when(appUserService.getAuthenticatedUser()).thenReturn(appUser);
        when(portfolioRepository.findActiveByIdAndAppUser(1, appUser)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> portfolioService.delete(1));
        verify(appUserService).getAuthenticatedUser();
        verify(portfolioRepository).findActiveByIdAndAppUser(1, appUser);
    }
}