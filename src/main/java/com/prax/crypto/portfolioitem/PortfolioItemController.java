package com.prax.crypto.portfolioitem;

import org.springframework.http.HttpStatus;
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
    public List<PortfolioItemResponseDto> findAll() {
        return this.service.findAll();
    }

    @GetMapping("/{id}")
    public PortfolioItemResponseDto findById(@PathVariable Integer id) {
        return this.service.findById(id);
    }

    @PostMapping
    public PortfolioItemResponseDto createPortfolioItem(@RequestBody PortfolioItemDto item) {
        return this.service.createPortfolioItem(item);
    }

    @PutMapping("/{id}")
    public PortfolioItemResponseDto updatePortfolioItem(@PathVariable Integer id, @RequestBody PortfolioItemDto item) {
        return this.service.updatePortfolioItem(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deletePortfolioItem(@PathVariable Integer id) {
        this.service.deletePortfolioItem(id);
    }
}
