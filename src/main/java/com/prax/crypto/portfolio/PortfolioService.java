package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUserService;
import com.prax.crypto.bitfinex.BitfinexService;
import com.prax.crypto.exception.PortfolioNotFoundException;
import com.prax.crypto.portfolio.dto.PortfolioDto;
import com.prax.crypto.portfolio.dto.PortfolioResponseDto;
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

        var portfolio = portfolioMapper.toEntity(item, currentUser);
        var savedPortfolio = portfolioRepository.save(portfolio);

        return portfolioMapper.toResponseDto(savedPortfolio, amountEur);
    }

    @Transactional
    public List<PortfolioResponseDto> findAll() {
        var currentUser = appUserService.getAuthenticatedUser();

        return portfolioRepository.findAllActiveByAppUser(currentUser)
                .stream()
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

        var portfolio = portfolioRepository.findActiveByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio item not found"));

        var amountEur = bitfinexService
                .getCryptoFxInEur(portfolio.getCurrency())
                .multiply(portfolio.getAmount());

        return portfolioMapper.toResponseDto(portfolio, amountEur);
    }

    @Transactional
    public PortfolioResponseDto update(Integer id, @Valid PortfolioDto item) {
        var currentUser = appUserService.getAuthenticatedUser();
        var amountEur = bitfinexService
                .getCryptoFxInEur(item.currency())
                .multiply(item.amount());

        var portfolio = portfolioRepository.findActiveByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio item not found"));

        portfolio.setAmount(item.amount());
        portfolio.setCurrency(item.currency());
        portfolio.setDateOfPurchase(item.dateOfPurchase());

        var savedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toResponseDto(savedPortfolio, amountEur);
    }

    public void delete(Integer id) {
        var currentUser = appUserService.getAuthenticatedUser();

        var portfolio = portfolioRepository.findActiveByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio item not found"));

        portfolio.setDeleted(true);
        portfolioRepository.save(portfolio);
    }
}
