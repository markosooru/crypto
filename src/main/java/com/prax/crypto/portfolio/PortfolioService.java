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
        var portfolioItem = portfolioMapper.toEntity(item);
        var savedPortfolioItem = portfolioRepository.save(portfolioItem);
        return portfolioMapper.toResponseDto(savedPortfolioItem);
    }

    public List<PortfolioResponseDto> findAll() {
        return portfolioRepository.findAll()
                .stream()
                .map(portfolioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PortfolioResponseDto findById(Integer id) {
        return this.portfolioRepository.findById(id)
                .map(portfolioMapper::toResponseDto)
                .orElse(null);
    }

    public PortfolioResponseDto updatePortfolioItem(Integer id, PortfolioDto item) {
        var existingItem = portfolioRepository.findById(id).orElse(null);
        if (existingItem == null) {
            return null;
        }
        var updatedItem = portfolioMapper.toEntity(item);
        var savedItem = portfolioRepository.save(updatedItem);
        return portfolioMapper.toResponseDto(savedItem);
    }

    public void deletePortfolioItem(Integer id) {
        portfolioRepository.deleteById(id);
    }
}
