package com.prax.crypto.portfolio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prax.crypto.account.AppUserDetailsService;
import com.prax.crypto.account.AppUserRepository;
import com.prax.crypto.account.AppUserService;
import com.prax.crypto.account.Role;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import com.prax.crypto.base.BaseIntTest;
import com.prax.crypto.portfolio.dto.PortfolioDto;
import com.prax.crypto.portfolio.dto.PortfolioResponseDto;
import com.prax.crypto.security.TokenService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PortfolioControllerIntTest extends BaseIntTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private TokenService tokenService;

    @Test
    @Transactional
    public void create_givenPortfolioDto_savesAndReturnsResponseDto() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var userToken = generateToken(email, "userPass", Role.ROLE_USER);

        var appUser = appUserRepository.findByEmail(email).orElseThrow();

        var portfolioDto = new PortfolioDto(
                new BigDecimal("0.12345678"),
                "ETH",
                LocalDateTime.now()
        );

        // when
        var jsonResponse = mockMvc.perform(post("/api/portfolio")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result.amount()).isEqualTo(portfolioDto.amount());
        assertThat(result.currency()).isEqualTo(portfolioDto.currency());
        assertThat(result.dateOfPurchase()).isEqualTo(portfolioDto.dateOfPurchase());
        assertThat(result.appUserId()).isEqualTo(appUser.getId());
        assertThat(result.amountEur()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @Transactional
    public void create_givenInvalidData_returnsBadRequest() throws Exception {
        // given
        var invalidPortfolioDto = new PortfolioDto(
                new BigDecimal("-0.12345678"),
                "",
                LocalDateTime.now().plusDays(1)
        );

        var adminToken = generateToken("admin@example.com", "adminPass", Role.ROLE_ADMIN);

        // when
        var jsonResponse = mockMvc.perform(post("/api/portfolio")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPortfolioDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});

        assertThat(result).containsEntry("amount", "must be greater than 0");
        assertThat(result).containsEntry("currency", "must not be empty");
        assertThat(result).containsEntry("dateOfPurchase", "must be a date in the past or in the present");
    }

    @Test
    @Transactional
    public void create_givenNoData_returnsBadRequest() throws Exception {
        // given
        var noData = "{}";
        var adminToken = generateToken("admin@example.com", "adminPass", Role.ROLE_ADMIN);

        // when
        var jsonResponse = mockMvc.perform(post("/api/portfolio")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noData))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});

        assertThat(result).containsEntry("amount", "must not be null");
        assertThat(result).containsEntry("currency", "must not be empty");
        assertThat(result).containsEntry("dateOfPurchase", "must not be null");
    }

    @Test
    @Transactional
    public void findAll_executes_returnsListOfActiveResponseDtos() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var userToken = generateToken(email, "userPass", Role.ROLE_USER);

        var appUser = appUserRepository.findByEmail(email).orElseThrow();

        var portfolio1 = new Portfolio(
                null,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );
        var portfolio2 = new Portfolio(
                null,
                new BigDecimal("0.12345678"),
                "ETH",
                LocalDateTime.now().minusDays(2),
                false,
                appUser
        );

        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);

        // when
        var jsonResponse = mockMvc.perform(get("/api/portfolio")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<List<PortfolioResponseDto>>() {});

        assertThat(result).hasSize(2);
        assertThat(result.stream().anyMatch(p ->
                p.amount().equals(portfolio1.getAmount()) &&
                p.currency().equals(portfolio1.getCurrency()) &&
                p.dateOfPurchase().equals(portfolio1.getDateOfPurchase()) &&
                p.appUserId().equals(appUser.getId())
        )).isTrue();
        assertThat(result.stream().anyMatch(p ->
                p.amount().equals(portfolio2.getAmount()) &&
                p.currency().equals(portfolio2.getCurrency()) &&
                p.dateOfPurchase().equals(portfolio2.getDateOfPurchase()) &&
                p.appUserId().equals(appUser.getId())
        )).isTrue();
    }

    @Test
    @Transactional
    public void findById_givenId_returnsResponseDto() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var userToken = generateToken(email, "userPass", Role.ROLE_USER);

        var appUser = appUserRepository.findByEmail(email).orElseThrow();

        var portfolio = new Portfolio(
                null,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );
        portfolio = portfolioRepository.save(portfolio);

        // when
        var jsonResponse = mockMvc.perform(get("/api/portfolio/{id}", portfolio.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result.amount()).isEqualTo(portfolio.getAmount());
        assertThat(result.currency()).isEqualTo(portfolio.getCurrency());
        assertThat(result.dateOfPurchase()).isEqualTo(portfolio.getDateOfPurchase());
        assertThat(result.appUserId()).isEqualTo(appUser.getId());
    }

    @Test
    @Transactional
    public void update_givenIdAndPortfolioDto_savesAndReturnsResponseDto() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var userToken = generateToken(email, "userPass", Role.ROLE_USER);

        var appUser = appUserRepository.findByEmail(email).orElseThrow();

        var portfolio = new Portfolio(
                null,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );
        portfolio = portfolioRepository.save(portfolio);

        var portfolioDto = new PortfolioDto(
                new BigDecimal("0.12345678"),
                "ETH",
                LocalDateTime.now().minusDays(1)
        );

        // when
        var jsonResponse = mockMvc.perform(put("/api/portfolio/{id}", portfolio.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result.amount()).isEqualTo(portfolioDto.amount());
        assertThat(result.currency()).isEqualTo(portfolioDto.currency());
        assertThat(result.dateOfPurchase()).isEqualTo(portfolioDto.dateOfPurchase());
        assertThat(result.appUserId()).isEqualTo(appUser.getId());
    }

    @Test
    @Transactional
    public void delete_givenId_setsPortfolioDeletedFlagTrue() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var userToken = generateToken(email, "userPass", Role.ROLE_USER);

        var appUser = appUserRepository.findByEmail(email).orElseThrow();

        var portfolio = new Portfolio(
                null,
                new BigDecimal("0.12345678"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                false,
                appUser
        );
        portfolio = portfolioRepository.save(portfolio);

        // when
        mockMvc.perform(delete("/api/portfolio/{id}", portfolio.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        // then
        var deletedPortfolio = portfolioRepository.findById(portfolio.getId());
        assertThat(deletedPortfolio.map(Portfolio::isDeleted).orElse(false)).isTrue();
    }

    private String generateToken(String email, String password, Role role) {
        if (appUserRepository.findByEmail(email).isEmpty()) {
            appUserService.createWithRole(new AppUserWithRoleDto(email, password, role));
        }
        var userDetails = appUserDetailsService.loadUserByUsername(email);
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        return tokenService.generateToken(authentication);
    }
}
