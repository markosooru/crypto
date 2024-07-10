package com.prax.crypto.portfolio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserService;
import com.prax.crypto.base.BaseIntTest;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PortfolioControllerIntTest extends BaseIntTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private AppUserService appUserService;

    @Test
    @Transactional
    public void create_givenPortfolioDto_savesAndReturnsResponseDto() throws Exception {
        // given
        var appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        var portfolioDto = new PortfolioDto(
                new BigDecimal("5.00"),
                "ETH",
                LocalDateTime.now(),
                appUser.getId()
        );

        // when
        var jsonResponse = mockMvc.perform(post("/api/portfolio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result.amount()).isEqualTo(portfolioDto.amount());
        assertThat(result.currency()).isEqualTo(portfolioDto.currency());
        assertThat(result.dateOfPurchase()).isEqualTo(portfolioDto.dateOfPurchase());
        assertThat(result.appUserId()).isEqualTo(portfolioDto.appUserId());
        assertThat(result.amountEur()).isGreaterThan(BigDecimal.ZERO);
    }

//    @Test
//    @Transactional
//    public void create_givenInvalidPortfolioDto_throwsException() throws Exception {
//        // given
//        var appUser = appUserService.create(new AppUser(
//                null,
//                "testuser",
//                "testuser_" + UUID.randomUUID() + "@example.com",
//                null
//        ));
//
//        var portfolioDto = new PortfolioDto(
//                new BigDecimal("-5.00"),
//                "ETH",
//                LocalDateTime.now(),
//                appUser.getId()
//        );
//
//        // when
//        mockMvc.perform(post("/api/portfolio")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(portfolioDto)))
//                // then
//                .andExpect(status().isBadRequest()) // Expecting a 400 Bad Request status
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.message").value("Invalid portfolio data"));
//    }

    @Test
    @Transactional
    public void findAll_executes_returnsListOfActiveResponseDtos() throws Exception {
        // given
        var appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        var responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        // when
        var jsonResponse = mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<List<PortfolioResponseDto>>() {});

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(responseDto);
    }

    @Test
    @Transactional
    public void findById_givenId_returnsResponseDto() throws Exception {
        // given
        var appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        var responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        // when
        var jsonResponse = mockMvc.perform(get("/api/portfolio/{id}", responseDto.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    @Transactional
    public void update_givenIdAndPortfolioDto_savesAndReturnsResponseDto() throws Exception {
        // given
        var appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        var responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        var portfolioDto = new PortfolioDto(
                new BigDecimal("5.00"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        );

        // when
        var jsonResponse = mockMvc.perform(put("/api/portfolio/{id}", responseDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result.amount()).isEqualTo(portfolioDto.amount());
        assertThat(result.currency()).isEqualTo(portfolioDto.currency());
        assertThat(result.dateOfPurchase()).isEqualTo(portfolioDto.dateOfPurchase());
        assertThat(result.appUserId()).isEqualTo(portfolioDto.appUserId());
        assertThat(result.id()).isEqualTo(responseDto.id());
    }

    @Test
    @Transactional
    public void delete_givenId_setsPortfolioDeletedFlagTrue() throws Exception {
        // given
        var appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        var responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        // when
        mockMvc.perform(delete("/api/portfolio/{id}", responseDto.id()))
                .andExpect(status().isNoContent());

        // then
        var deletedPortfolio = portfolioRepository.findById(responseDto.id());
        assertThat(deletedPortfolio.map(Portfolio::isDeleted).orElse(false)).isTrue();
    }
}