package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserRepository;
import com.prax.crypto.bitfinex.BitfinexService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AppUserRepository appUserRepository;

    private AppUser getCurrentUser() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return appUserRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public PortfolioResponseDto create(@Valid PortfolioDto item) {
        var currentUser = getCurrentUser();
        var amountEur = bitfinexService
                .getCryptoFxInEur(item.currency())
                .multiply(item.amount());

        var portfolioItem = portfolioMapper.toEntity(item);
        portfolioItem.setAppUser(currentUser);

        var savedPortfolioItem = portfolioRepository
                .save(portfolioItem);
        return portfolioMapper.toResponseDto(savedPortfolioItem, amountEur);
    }

    @Transactional
    public List<PortfolioResponseDto> findAll() {
        var currentUser = getCurrentUser();

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
        var currentUser = getCurrentUser();

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
        var currentUser = getCurrentUser();

        portfolioRepository.findActiveById(id)
                .filter(portfolio -> portfolio.getAppUser().equals(currentUser))
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

        var amountEur = bitfinexService
                .getCryptoFxInEur(item.currency())
                .multiply(item.amount());

        var updatedItem = portfolioMapper.toEntity(item);
        updatedItem.setId(id);
        updatedItem.setAppUser(currentUser);

        var savedItem = portfolioRepository.save(updatedItem);
        return portfolioMapper.toResponseDto(savedItem, amountEur);
    }

    public void delete(Integer id) {
        var currentUser = getCurrentUser();

        var item = portfolioRepository.findActiveById(id)
                .filter(portfolio -> portfolio.getAppUser().equals(currentUser))
                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

        item.setDeleted(true);
        portfolioRepository.save(item);
    }
}
