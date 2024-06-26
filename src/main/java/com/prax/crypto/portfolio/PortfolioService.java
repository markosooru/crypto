package com.prax.crypto.portfolio;

import com.prax.crypto.bitfinex.BitfinexService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final BitfinexService bitfinexService;

    public PortfolioService(PortfolioRepository portfolioRepository, PortfolioMapper portfolioMapper, BitfinexService bitfinexService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
        this.bitfinexService = bitfinexService;
    }

    @Transactional
    public PortfolioResponseDto createPortfolio(PortfolioDto item) {
        BigDecimal currentPriceInEUR = bitfinexService
                .getTicker(item.currency())
                .lastPrice();
        BigDecimal amountEur = currentPriceInEUR.multiply(item.amount());

        var portfolioItem = portfolioMapper.toEntity(item);
        var savedPortfolioItem = portfolioRepository.save(portfolioItem);
        return portfolioMapper.toResponseDto(savedPortfolioItem, amountEur);
    }

    @Transactional
    public List<PortfolioResponseDto> findAll() {
        return portfolioRepository.findAllActive()
                .stream()
                .map(portfolio -> {
                    BigDecimal currentPriceInEUR = bitfinexService
                            .getTicker(portfolio.getCurrency())
                            .lastPrice();
                    BigDecimal amountEur = currentPriceInEUR.multiply(portfolio.getAmount());
                    return portfolioMapper.toResponseDto(portfolio, amountEur);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioResponseDto findById(Integer id) {
        return portfolioRepository.findActiveById(id)
                .map(portfolio -> {
                    BigDecimal currentPriceInEUR = bitfinexService
                            .getTicker(portfolio.getCurrency())
                            .lastPrice();
                    BigDecimal amountEur = currentPriceInEUR.multiply(portfolio.getAmount());
                    return portfolioMapper.toResponseDto(portfolio, amountEur);
                })
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));
    }

    @Transactional
    public PortfolioResponseDto updatePortfolio(Integer id, PortfolioDto item) {
        portfolioRepository
                .findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

        BigDecimal currentPriceInEUR = bitfinexService
                .getTicker(item.currency())
                .lastPrice();
        BigDecimal amountEur = currentPriceInEUR.multiply(item.amount());

        var updatedItem = portfolioMapper.toEntity(item);
        var savedItem = portfolioRepository.save(updatedItem);
        return portfolioMapper.toResponseDto(savedItem, amountEur);
    }

    public void deletePortfolio(Integer id) {
        Portfolio item = portfolioRepository
                .findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));
        item.setDeleted(true);
        portfolioRepository.save(item);
    }
}
