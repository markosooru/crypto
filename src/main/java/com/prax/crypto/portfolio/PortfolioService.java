package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUserService;
import com.prax.crypto.bitfinex.BitfinexService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final BitfinexService bitfinexService;
    private final AppUserService appUserService;

    @Transactional
    public PortfolioResponseDto create(@Valid PortfolioDto item) {
        var currentUser = appUserService.getAuthenticatedUser();
        var amountEur = bitfinexService
                .getCryptoFxInEur(item.currency())
                .multiply(item.amount());

        var portfolioItem = portfolioMapper.toEntity(item, currentUser);

        var savedPortfolioItem = portfolioRepository
                .save(portfolioItem);
        return portfolioMapper.toResponseDto(savedPortfolioItem, amountEur);
    }

    @Transactional
    public List<PortfolioResponseDto> findAll() {
        var currentUser = appUserService.getAuthenticatedUser();

        return portfolioRepository.findAllActive()
                .stream()
                .filter(portfolio -> portfolio.getAppUser().equals(currentUser))
                .map(portfolio -> {
                    var amountEur = bitfinexService
                            .getCryptoFxInEur(portfolio.getCurrency())
                            .multiply(portfolio.getAmount());
                    return portfolioMapper.toResponseDto(portfolio, amountEur);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioResponseDto findById(Integer id) {
        var currentUser = appUserService.getAuthenticatedUser();

        return portfolioRepository.findActiveById(id)
                .filter(portfolio -> portfolio.getAppUser().equals(currentUser))
                .map(portfolio -> {
                    var amountEur = bitfinexService
                            .getCryptoFxInEur(portfolio.getCurrency())
                            .multiply(portfolio.getAmount());
                    return portfolioMapper.toResponseDto(portfolio, amountEur);
                })
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));
    }

    @Transactional
    public PortfolioResponseDto update(Integer id, @Valid PortfolioDto item) {
        var currentUser = appUserService.getAuthenticatedUser();

        portfolioRepository.findActiveById(id)
                .filter(portfolio -> portfolio.getAppUser().equals(currentUser))
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

        var amountEur = bitfinexService
                .getCryptoFxInEur(item.currency())
                .multiply(item.amount());

        var updatedItem = portfolioMapper.toEntity(item, currentUser);
        updatedItem.setId(id);

        var savedItem = portfolioRepository.save(updatedItem);
        return portfolioMapper.toResponseDto(savedItem, amountEur);
    }

    public void delete(Integer id) {
        var currentUser = appUserService.getAuthenticatedUser();

        var item = portfolioRepository.findActiveById(id)
                .filter(portfolio -> portfolio.getAppUser().equals(currentUser))
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

        item.setDeleted(true);
        portfolioRepository.save(item);
    }
}
