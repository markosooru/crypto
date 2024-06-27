package com.prax.crypto.portfolio;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioService(PortfolioRepository portfolioRepository, PortfolioMapper portfolioMapper) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
    }

    public PortfolioResponseDto createPortfolioItem(PortfolioDto item) {
        var portfolioItem = this.portfolioMapper.toPortfolio(item);
        var savedPortfolioItem = this.portfolioRepository.save(portfolioItem);
        return portfolioMapper.toPortfolioResponseDto(savedPortfolioItem);
    }

    public List<PortfolioResponseDto> findAll() {
        return this.portfolioRepository.findAll()
                .stream()
                .map(this.portfolioMapper::toPortfolioResponseDto)
                .collect(Collectors.toList());
    }

    public PortfolioResponseDto findById(Integer id) {
        return this.portfolioRepository.findById(id)
                .map(this.portfolioMapper::toPortfolioResponseDto)
                .orElse(null);
    }

    public PortfolioResponseDto updatePortfolioItem(Integer id, PortfolioDto item) {
        var existingItem = this.portfolioRepository.findById(id).orElse(null);
        if (existingItem == null) {
            return null;
        }
        var updatedItem = this.portfolioMapper.toPortfolio(item);
        var savedItem = this.portfolioRepository.save(updatedItem);
        return this.portfolioMapper.toPortfolioResponseDto(savedItem);
    }

    public void deletePortfolioItem(Integer id) {
        this.portfolioRepository.deleteById(id);
    }
}
