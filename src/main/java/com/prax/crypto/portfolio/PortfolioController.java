package com.prax.crypto.portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/portfolioitems")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public List<PortfolioResponseDto> findAll() {
        return this.portfolioService.findAll();
    }

    @GetMapping("/{id}")
    public PortfolioResponseDto findById(@PathVariable Integer id) {
        return this.portfolioService.findById(id);
    }

    @PostMapping
    public PortfolioResponseDto createPortfolioItem(@RequestBody PortfolioDto item) {
        return this.portfolioService.createPortfolioItem(item);
    }

    @PutMapping("/{id}")
    public PortfolioResponseDto updatePortfolioItem(@PathVariable Integer id, @RequestBody PortfolioDto item) {
        return this.portfolioService.updatePortfolioItem(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deletePortfolioItem(@PathVariable Integer id) {
        this.portfolioService.deletePortfolioItem(id);
    }
}
