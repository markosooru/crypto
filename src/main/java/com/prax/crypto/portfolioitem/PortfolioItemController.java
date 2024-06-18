package com.prax.crypto.portfolioitem;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/portfolioitems")
public class PortfolioItemController {

    private final PortfolioItemService service;

    public PortfolioItemController(PortfolioItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<PortfolioItem> findAll() {
        return this.service.findAll();
    }

    @GetMapping("/{id}")
    public PortfolioItem findById(@PathVariable Integer id) {
        return this.service.findById(id);
    }

    @PostMapping
    public PortfolioItem createPortfolioItem(@RequestBody PortfolioItem item) {
        return this.service.createPortfolioItem(item);
    }

    @PutMapping("/{id}")
    public PortfolioItem updatePortfolioItem(@PathVariable Integer id, @RequestBody PortfolioItem item) {
        return this.service.updatePortfolioItem(id, item);
    }

    @DeleteMapping("/{id}")
    public void deletePortfolioItem(@PathVariable Integer id) {
        this.service.deletePortfolioItem(id);
    }
}
