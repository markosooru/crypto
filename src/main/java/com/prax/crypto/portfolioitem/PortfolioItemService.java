package com.prax.crypto.portfolioitem;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioItemService {

    private final PortfolioItemRepository repository;
    private final PortfolioItemMapper mapper;

    public PortfolioItemService(PortfolioItemRepository repository, PortfolioItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PortfolioItemResponseDto createPortfolioItem(PortfolioItemDto item) {
        var portfolioItem = this.mapper.toEntity(item);
        var savedPortfolioItem = this.repository.save(portfolioItem);
        return mapper.toResponseDto(savedPortfolioItem);
    }

    public List<PortfolioItemResponseDto> findAll() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PortfolioItemResponseDto findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::toResponseDto)
                .orElse(null);
    }

    public PortfolioItemResponseDto updatePortfolioItem(Integer id, PortfolioItemDto item) {
        var existingItem = this.repository.findById(id).orElse(null);
        if (existingItem == null) {
            return null;
        }
        var updatedItem = this.mapper.toEntity(item);
        updatedItem.setId(id);
        var savedItem = this.repository.save(updatedItem);
        return this.mapper.toResponseDto(savedItem);
    }

    public void deletePortfolioItem(Integer id) {
        this.repository.deleteById(id);
    }
}
