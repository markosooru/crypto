package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.bitfinex.BitfinexService;
import com.prax.crypto.bitfinex.Ticker;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private PortfolioDto portfolioDto;
    private Portfolio portfolio;
    private PortfolioResponseDto portfolioResponseDto;
    private Ticker ticker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolioDto = new PortfolioDto(
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1
        );

        AppUser appUser = AppUser.builder().id(1).build();
        portfolio = new Portfolio(
                1,
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );

        portfolioResponseDto = new PortfolioResponseDto(
                new BigDecimal("2.0"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                1,
                new BigDecimal("60000.0")
        );

        ticker = new Ticker(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("30000.0"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    @Test
    void createPortfolio() {
        when(bitfinexService.getTicker(portfolioDto.currency())).thenReturn(ticker);
        when(portfolioMapper.toEntity(portfolioDto)).thenReturn(portfolio);
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        PortfolioResponseDto result = portfolioService.createPortfolio(portfolioDto);

        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).save(portfolioCaptor.capture());
        assertEquals(portfolio, portfolioCaptor.getValue());
    }

    @Test
    void findAll() {
        when(portfolioRepository.findAllActive()).thenReturn(List.of(portfolio));
        when(bitfinexService.getTicker(portfolio.getCurrency())).thenReturn(ticker);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        List<PortfolioResponseDto> result = portfolioService.findAll();

        assertEquals(1, result.size());
        assertEquals(portfolioResponseDto, result.getFirst());
        verify(portfolioRepository).findAllActive();
    }

    @Test
    void findById() {
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.of(portfolio));
        when(bitfinexService.getTicker(portfolio.getCurrency())).thenReturn(ticker);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        PortfolioResponseDto result = portfolioService.findById(1);

        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).findActiveById(1);
    }

    @Test
    void findById_NotFound() {
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> portfolioService.findById(1));

        assertEquals("Portfolio item not found", exception.getMessage());
        verify(portfolioRepository).findActiveById(1);
    }

    @Test
    void updatePortfolio() {
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.of(portfolio));
        when(bitfinexService.getTicker(portfolioDto.currency())).thenReturn(ticker);
        when(portfolioMapper.toEntity(portfolioDto)).thenReturn(portfolio);
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);
        when(portfolioMapper.toResponseDto(any(Portfolio.class), any(BigDecimal.class)))
                .thenReturn(portfolioResponseDto);

        PortfolioResponseDto result = portfolioService.updatePortfolio(1, portfolioDto);

        assertEquals(portfolioResponseDto, result);
        verify(portfolioRepository).findActiveById(1);
        verify(portfolioRepository).save(portfolioCaptor.capture());
        assertEquals(portfolio, portfolioCaptor.getValue());
    }

    @Test
    void updatePortfolio_NotFound() {
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> portfolioService.updatePortfolio(1, portfolioDto));

        assertEquals("Portfolio item not found", exception.getMessage());
        verify(portfolioRepository).findActiveById(1);
    }

    @Test
    void deletePortfolio() {
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.of(portfolio));

        portfolioService.deletePortfolio(1);

        assertTrue(portfolio.isDeleted());
        verify(portfolioRepository).save(portfolioCaptor.capture());
        assertEquals(portfolio, portfolioCaptor.getValue());
    }

    @Test
    void deletePortfolio_NotFound() {
        when(portfolioRepository.findActiveById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> portfolioService.deletePortfolio(1));

        assertEquals("Portfolio item not found", exception.getMessage());
        verify(portfolioRepository).findActiveById(1);
    }
}