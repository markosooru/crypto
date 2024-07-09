package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserService;
import com.prax.crypto.base.BaseIntTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        AppUser appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        PortfolioDto portfolioDto = new PortfolioDto(
                new BigDecimal("5.00"),
                "ETH",
                LocalDateTime.now(),
                appUser.getId()
        );

        // when
        String jsonResponse = mockMvc.perform(post("/api/portfolio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        PortfolioResponseDto result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result.amount()).isEqualTo(portfolioDto.amount());
        assertThat(result.currency()).isEqualTo(portfolioDto.currency());
        assertThat(result.dateOfPurchase()).isEqualTo(portfolioDto.dateOfPurchase());
        assertThat(result.appUserId()).isEqualTo(portfolioDto.appUserId());
        assertThat(result.amountEur()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @Transactional
    public void findAll_executes_returnsListOfActiveResponseDtos() throws Exception {
        // given
        AppUser appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        PortfolioResponseDto responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        // when
        String jsonResponse = mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        PortfolioResponseDto[] result = objectMapper.readValue(jsonResponse, PortfolioResponseDto[].class);

        assertThat(result[0]).isEqualTo(responseDto);
    }

    @Test
    @Transactional
    public void findById_givenId_returnsResponseDto() throws Exception {
        // given
        AppUser appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        PortfolioResponseDto responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        // when
        String jsonResponse = mockMvc.perform(get("/api/portfolio/{id}", responseDto.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        PortfolioResponseDto result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    @Transactional
    public void update_givenIdAndPortfolioDto_savesAndReturnsResponseDto() throws Exception {
        // given
        AppUser appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        PortfolioResponseDto responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        PortfolioDto portfolioDto = new PortfolioDto(
                new BigDecimal("5.00"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        );

        // when
        String jsonResponse = mockMvc.perform(put("/api/portfolio/{id}", responseDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        PortfolioResponseDto result = objectMapper.readValue(jsonResponse, PortfolioResponseDto.class);

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
        AppUser appUser = appUserService.create(new AppUser(
                null,
                "testuser",
                "testuser_" + UUID.randomUUID() + "@example.com",
                null
        ));

        PortfolioResponseDto responseDto = portfolioService.create(new PortfolioDto(
                new BigDecimal("2.50"),
                "BTC",
                LocalDateTime.now().minusDays(1),
                appUser.getId()
        ));

        // when
        mockMvc.perform(delete("/api/portfolio/{id}", responseDto.id()))
                .andExpect(status().isNoContent());

        // then
        Optional<Portfolio> deletedPortfolio = portfolioRepository.findById(responseDto.id());
        assertThat(deletedPortfolio.map(Portfolio::isDeleted).orElse(false)).isTrue();
    }
}