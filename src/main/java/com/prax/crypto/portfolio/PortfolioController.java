package com.prax.crypto.portfolio;

import lombok.AllArgsConstructor;
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
@RequestMapping("api/portfolios")
@AllArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public PortfolioResponseDto create(@RequestBody PortfolioDto item) {
        return portfolioService.create(item);
    }

    @GetMapping
    public List<PortfolioResponseDto> findAll() {
        return portfolioService.findAll();
    }

    @GetMapping("/{id}")
    public PortfolioResponseDto findById(@PathVariable Integer id) {
        return portfolioService.findById(id);
    }

    @PutMapping("/{id}")
    public PortfolioResponseDto update(@PathVariable Integer id, @RequestBody PortfolioDto item) {
        return portfolioService.update(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        portfolioService.delete(id);
    }
}
