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
        return portfolioService.findAll();
    }

    @GetMapping("/{id}")
    public PortfolioResponseDto findById(@PathVariable Integer id) {
        return portfolioService.findById(id);
    }

    @PostMapping
    public PortfolioResponseDto createPortfolioItem(@RequestBody PortfolioDto item) {
        return portfolioService.createPortfolioItem(item);
    }

    @PutMapping("/{id}")
    public PortfolioResponseDto updatePortfolioItem(@PathVariable Integer id, @RequestBody PortfolioDto item) {
        return portfolioService.updatePortfolioItem(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deletePortfolioItem(@PathVariable Integer id) {
        portfolioService.deletePortfolioItem(id);
    }
}
