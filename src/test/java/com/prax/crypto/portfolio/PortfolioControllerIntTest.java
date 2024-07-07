package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserRepository;
import com.prax.crypto.account.AppUserService;
import com.prax.crypto.base.BaseIntTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PortfolioControllerIntTest extends BaseIntTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private AppUserService appUserService;

    @AfterEach
    public void tearDown() {
        portfolioRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void FindAll_ReturnsListOfActiveResponseDtos() throws Exception {
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
        String jsonResponse = mockMvc.perform(get("/api/portfolios"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        PortfolioResponseDto[] responseDtos = objectMapper.readValue(jsonResponse, PortfolioResponseDto[].class);

        assertThat(responseDtos[0]).isEqualTo(responseDto);
    }
//
//    @Test
//    public void FindById_ReturnsResponseDto() throws Exception {
//        mockMvc.perform(get("/api/portfolios/{id}", portfolio.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.amount").value(portfolio.getAmount()))
//                .andExpect(jsonPath("$.currency").value(portfolio.getCurrency()))
//                .andExpect(jsonPath("$.dateOfPurchase").value(portfolio.getDateOfPurchase()
//                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))))
//                .andExpect(jsonPath("$.appUserId").value(portfolio.getAppUser().getId()))
//                .andExpect(jsonPath("$.amountEur").isNotEmpty());
//    }
//
//    @Test
//    public void Create_SavesPortfolioAndReturnsResponseDto() throws Exception {
//        PortfolioDto portfolioDto = new PortfolioDto(
//                new BigDecimal("5.0"),
//                "ETH",
//                LocalDateTime.now(),
//                appUser.getId()
//        );
//
//        mockMvc.perform(post("/api/portfolios")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(portfolioDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.amount").value(portfolioDto.amount()))
//                .andExpect(jsonPath("$.currency").value(portfolioDto.currency()))
//                .andExpect(jsonPath("$.dateOfPurchase").value(portfolioDto.dateOfPurchase()
//                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))))
//                .andExpect(jsonPath("$.appUserId").value(portfolioDto.appUserId()))
//                .andExpect(jsonPath("$.amountEur").isNotEmpty());
//    }
//
//    @Test
//    public void Update_SavesPortfolioAndReturnsResponseDto() throws Exception {
//        PortfolioDto portfolioDto = new PortfolioDto(
//                new BigDecimal("3.0"),
//                "BTC",
//                LocalDateTime.now(),
//                appUser.getId()
//        );
//
//        mockMvc.perform(put("/api/portfolios/{id}", portfolio.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(portfolioDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.amount").value(portfolioDto.amount()))
//                .andExpect(jsonPath("$.currency").value(portfolioDto.currency()))
//                .andExpect(jsonPath("$.dateOfPurchase").value(portfolioDto.dateOfPurchase()
//                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))))
//                .andExpect(jsonPath("$.appUserId").value(portfolioDto.appUserId()))
//                .andExpect(jsonPath("$.amountEur").isNotEmpty());
//    }
//
//    @Test
//    public void Delete_SetsPortfolioAsDeleted() throws Exception {
//        mockMvc.perform(delete("/api/portfolios/{id}", portfolio.getId()))
//                .andExpect(status().isNoContent());
//    }
}