package com.prax.crypto.account;

import com.prax.crypto.account.dto.AppUserDto;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import com.prax.crypto.portfolio.Portfolio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppUserMapperTest {

    private final AppUserMapper appUserMapper = new AppUserMapper();

    @Test
    void toEntity_givenAppUserDto_mapsDtoToEntity() {
        // given
        var appUserDto = new AppUserDto(
                "test@example.com",
                "testpass"
        );

        // when
        var result = appUserMapper.toEntity(appUserDto);

        // then
        assertEquals(appUserDto.email(), result.getEmail());
        assertEquals(appUserDto.password(), result.getPassword());
        assertEquals(Role.ROLE_USER, result.getRole());
    }

    @Test
    void toEntityWithRole_givenAppUserDto_mapsDtoToEntity() {
        // given
        var appUserWithRoleDto = new AppUserWithRoleDto(
                "test@example.com",
                "testpass",
                Role.ROLE_ADMIN
        );

        // when
        var result = appUserMapper.toEntityWithRole(appUserWithRoleDto);

        // then
        assertEquals(appUserWithRoleDto.email(), result.getEmail());
        assertEquals(appUserWithRoleDto.password(), result.getPassword());
        assertEquals(Role.ROLE_ADMIN, result.getRole());
    }

    @Test
    void toResponseDto_givenEntity_mapsEntityToResponseDto() {
        // given
        var appUser = new AppUser(
                1,
                "test@example.com",
                "testpass",
                null,
                Role.ROLE_USER
        );
        appUser.setPortfolioItems(List.of(
                new Portfolio(
                        1,
                        new BigDecimal("0.12345678"),
                        "BTC",
                        LocalDateTime.now().minusDays(1),
                        false,
                        appUser
                )
        ));

        // when
        var result = appUserMapper.toResponseDto(appUser);

        // then
        assertEquals(appUser.getId(), result.id());
        assertEquals(appUser.getEmail(), result.email());
        assertEquals(appUser.getPortfolioItems().getFirst().getId(),
                result.portfolioItems().getFirst().getId());
        assertEquals(appUser.getPortfolioItems().getFirst().getAmount(),
                result.portfolioItems().getFirst().getAmount());
        assertEquals(appUser.getPortfolioItems().getFirst().getCurrency(),
                result.portfolioItems().getFirst().getCurrency());
        assertEquals(appUser.getPortfolioItems().getFirst().getDateOfPurchase(),
                result.portfolioItems().getFirst().getDateOfPurchase());
        assertEquals(appUser.getPortfolioItems().getFirst().isDeleted(),
                result.portfolioItems().getFirst().isDeleted());
    }
}