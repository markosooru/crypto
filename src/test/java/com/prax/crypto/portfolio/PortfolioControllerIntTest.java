package com.prax.crypto.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class PortfolioControllerIntTest {

    @Container
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void testCreatePortfolioItem() throws Exception {
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
    public void testUpdatePortfolioItem() throws Exception {
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
    public void testDeletePortfolioItem() throws Exception {
        mockMvc.perform(delete("/api/portfolios/{id}", portfolio.getId()))
                .andExpect(status().isNoContent());
    }
}