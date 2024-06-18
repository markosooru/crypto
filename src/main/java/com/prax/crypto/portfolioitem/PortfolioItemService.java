package com.prax.crypto.portfolioitem;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioItemService {

    private final PortfolioItemRepository repository;

    public PortfolioItemService(PortfolioItemRepository repository) {
        this.repository = repository;
    }

    public PortfolioItem createPortfolioItem(PortfolioItem item) {
        return this.repository.save(item);
    }

    public List<PortfolioItem> findAll() {
        return this.repository.findAll();
    }

    public PortfolioItem findById(Integer id) {
        return this.repository.findById(id).orElse(null);
    }

    public PortfolioItem updatePortfolioItem(Integer id, PortfolioItem item) {
        var existingItem = this.repository.findById(id).orElse(null);
        if (existingItem == null) {
            return null;
        }
        item.setId(id);
        return this.repository.save(item);
    }

    public void deletePortfolioItem(Integer id) {
        this.repository.deleteById(id);
    }
}
