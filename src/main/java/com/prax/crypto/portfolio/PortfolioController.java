package com.prax.crypto.portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
        return portfolioService.createPortfolio(item);
    }

    @PutMapping("/{id}")
    public PortfolioResponseDto updatePortfolioItem(@PathVariable Integer id, @RequestBody PortfolioDto item) {
        return portfolioService.updatePortfolio(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deletePortfolioItem(@PathVariable Integer id) {
        portfolioService.deletePortfolio(id);
    }
}
