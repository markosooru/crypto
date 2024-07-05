package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserRepository;
import com.prax.crypto.base.BaseIntTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PortfolioControllerIntTest extends BaseIntTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    private Portfolio portfolio;
    private AppUser appUser;

    @BeforeEach
    public void setUp() {
        appUser = appUserRepository.save(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        portfolio = portfolioRepository.save(new Portfolio(
                null,
                new BigDecimal("2.5"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        ));

        appUser.setPortfolioItems(List.of(portfolio));
    }

    @AfterEach
    public void tearDown() {
        portfolioRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void testFindAll() throws Exception {
        mockMvc.perform(get("/api/portfolios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].amount").value(portfolio.getAmount()))
                .andExpect(jsonPath("$[0].currency").value(portfolio.getCurrency()))
                .andExpect(jsonPath("$[0].dateOfPurchase").value(portfolio.getDateOfPurchase()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))))
                .andExpect(jsonPath("$[0].appUserId").value(portfolio.getAppUser().getId()))
                .andExpect(jsonPath("$[0].amountEur").isNotEmpty());
    }

    @Test
    public void testFindById() throws Exception {
        mockMvc.perform(get("/api/portfolios/{id}", portfolio.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(portfolio.getAmount()))
                .andExpect(jsonPath("$.currency").value(portfolio.getCurrency()))
                .andExpect(jsonPath("$.dateOfPurchase").value(portfolio.getDateOfPurchase()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))))
                .andExpect(jsonPath("$.appUserId").value(portfolio.getAppUser().getId()))
                .andExpect(jsonPath("$.amountEur").isNotEmpty());
    }

    @Test
    public void testCreate() throws Exception {
        PortfolioDto portfolioDto = new PortfolioDto(
                new BigDecimal("5.0"),
                "ETH",
                LocalDateTime.now(),
                appUser.getId()
        );

        mockMvc.perform(post("/api/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(portfolioDto.amount()))
                .andExpect(jsonPath("$.currency").value(portfolioDto.currency()))
                .andExpect(jsonPath("$.dateOfPurchase").value(portfolioDto.dateOfPurchase()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))))
                .andExpect(jsonPath("$.appUserId").value(portfolioDto.appUserId()))
                .andExpect(jsonPath("$.amountEur").isNotEmpty());
    }

    @Test
    public void testUpdate() throws Exception {
        PortfolioDto portfolioDto = new PortfolioDto(
                new BigDecimal("3.0"),
                "BTC",
                LocalDateTime.now(),
                appUser.getId()
        );

        mockMvc.perform(put("/api/portfolios/{id}", portfolio.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(portfolioDto.amount()))
                .andExpect(jsonPath("$.currency").value(portfolioDto.currency()))
                .andExpect(jsonPath("$.dateOfPurchase").value(portfolioDto.dateOfPurchase()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))))
                .andExpect(jsonPath("$.appUserId").value(portfolioDto.appUserId()))
                .andExpect(jsonPath("$.amountEur").isNotEmpty());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/portfolios/{id}", portfolio.getId()))
                .andExpect(status().isNoContent());
    }
}