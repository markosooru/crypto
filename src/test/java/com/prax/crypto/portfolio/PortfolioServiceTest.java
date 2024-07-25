package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.bitfinex.BitfinexService;
import com.prax.crypto.bitfinex.Ticker;
import com.prax.crypto.portfolio.dto.PortfolioDto;
import com.prax.crypto.portfolio.dto.PortfolioResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Captor
    private ArgumentCaptor<Portfolio> portfolioCaptor;

    @Test
    void create_givenPortfolioDto_savesAndReturnsResponseDto() {
        // given
        var appUser = AppUser.builder().id(1).build();
        var ticker = Ticker.builder().lastPrice(new BigDecimal("30000.0")).build();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioDto = new PortfolioDto(
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("60000.0")
        );

        // mock
        when(bitfinexService.getCryptoFxInEur(portfolioDto.currency())).thenReturn(ticker);
        when(portfolioMapper.toEntity(portfolioDto)).thenReturn(portfolio);
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        // when
        var result = portfolioService.create(portfolioDto);

        // then
        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).save(portfolioCaptor.capture());
        assertEquals(portfolio, portfolioCaptor.getValue());
    }

    @Test
    void findAll_executes_returnsListOfActivePortfolioDtos() {
        // given
        var appUser = AppUser.builder().id(1).build();
        var ticker = Ticker.builder().lastPrice(new BigDecimal("30000.0")).build();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("60000.0")
        );

        // mock
        when(portfolioRepository.findAllActive()).thenReturn(List.of(portfolio));
        when(bitfinexService.getCryptoFxInEur(portfolio.getCurrency())).thenReturn(ticker);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        // when
        var result = portfolioService.findAll();

        // then
        assertEquals(1, result.size());
        assertEquals(portfolioResponseDto, result.getFirst());
        verify(portfolioRepository).findAllActive();
    }

    @Test
    void findById_givenId_returnsCorrespondingResponseDtoIfActive() {
        // given
        var appUser = AppUser.builder().id(1).build();
        var ticker = Ticker.builder().lastPrice(new BigDecimal("30000.0")).build();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("60000.0")
        );

        // mock
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.of(portfolio));
        when(bitfinexService.getCryptoFxInEur(portfolio.getCurrency())).thenReturn(ticker);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        // when
        var result = portfolioService.findById(1);

        // then
        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).findActiveById(1);
    }

    @Test
    void findById_givenWrongId_throwsEntityNotFoundException() {
        // mock
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> portfolioService.findById(1));
        verify(portfolioRepository).findActiveById(1);
    }

    @Test
    void update_givenIdAndPortfolioDto_savesAndReturnsResponseDto() {
        // given
        var appUser = AppUser.builder().id(1).build();
        var ticker = Ticker.builder().lastPrice(new BigDecimal("30000.0")).build();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        var portfolioDto = new PortfolioDto(
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1
        );

        var portfolioResponseDto = new PortfolioResponseDto(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("60000.0")
        );

        // mock
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.of(portfolio));
        when(bitfinexService.getCryptoFxInEur(portfolioDto.currency())).thenReturn(ticker);
        when(portfolioMapper.toEntity(portfolioDto)).thenReturn(portfolio);
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        // when
        var result = portfolioService.update(1, portfolioDto);

        // then
        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).findActiveById(1);
        verify(portfolioRepository).save(portfolioCaptor.capture());
        assertEquals(portfolio, portfolioCaptor.getValue());
    }

    @Test
    void update_givenWrongId_throwsEntityNotFoundException() {
        // given
        var portfolioDto = new PortfolioDto(
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1
        );

        // mock
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> portfolioService.update(1, portfolioDto));
        verify(portfolioRepository).findActiveById(1);
    }

    @Test
    void delete_givenId_setsDeletedFlagToTrue() {
        // given
        var appUser = AppUser.builder().id(1).build();

        var portfolio = new Portfolio(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        // mock
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.of(portfolio));

        // when
        portfolioService.delete(1);

        // then
        assertTrue(portfolio.isDeleted());
        verify(portfolioRepository).save(portfolioCaptor.capture());
        assertEquals(portfolio, portfolioCaptor.getValue());
    }

    @Test
    void delete_givenWrongId_throwsEntityNotFoundException() {
        // mock
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> portfolioService.delete(1));
        verify(portfolioRepository).findActiveById(1);
    }
}