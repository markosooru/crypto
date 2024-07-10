package com.prax.crypto.portfolio;

import com.prax.crypto.bitfinex.BitfinexService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final BitfinexService bitfinexService;

    @Transactional
    public PortfolioResponseDto create(@Valid PortfolioDto item) {
        var currentPriceInEUR = bitfinexService
                .getTicker(item.currency())
                .lastPrice();
        var amountEur = currentPriceInEUR.multiply(item.amount());

        var portfolioItem = portfolioMapper.toEntity(item);
        var savedPortfolioItem = portfolioRepository.save(portfolioItem);
        return portfolioMapper.toResponseDto(savedPortfolioItem, amountEur);
    }

    @Transactional
    public List<PortfolioResponseDto> findAll() {
        return portfolioRepository.findAllActive()
                .stream()
                .map(portfolio -> {
                    var currentPriceInEUR = bitfinexService
                            .getTicker(portfolio.getCurrency())
                            .lastPrice();
                    var amountEur = currentPriceInEUR.multiply(portfolio.getAmount());
                    return portfolioMapper.toResponseDto(portfolio, amountEur);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioResponseDto findById(Integer id) {
        return portfolioRepository.findActiveById(id)
                .map(portfolio -> {
                    var currentPriceInEUR = bitfinexService
                            .getTicker(portfolio.getCurrency())
                            .lastPrice();
                    var amountEur = currentPriceInEUR.multiply(portfolio.getAmount());
                    return portfolioMapper.toResponseDto(portfolio, amountEur);
                })
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));
    }

    @Transactional
    public PortfolioResponseDto update(Integer id, @Valid PortfolioDto item) {
        portfolioRepository
                .findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

        var currentPriceInEUR = bitfinexService
                .getTicker(item.currency())
                .lastPrice();
        var amountEur = currentPriceInEUR.multiply(item.amount());

        var updatedItem = portfolioMapper.toEntity(item);
        updatedItem.setId(id);
        var savedItem = portfolioRepository.save(updatedItem);
        return portfolioMapper.toResponseDto(savedItem, amountEur);
    }

    public void delete(Integer id) {
        Portfolio item = portfolioRepository
                .findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));
        item.setDeleted(true);
        portfolioRepository.save(item);
    }
}
