package com.prax.crypto.account;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class AppUserMapper {

    public AppUser toEntity(@Valid AppUserDto appUserDto) {
        return AppUser.builder()
                .email(appUserDto.email())
                .password(appUserDto.password())
                .role(Role.ROLE_USER)
                .build();
    }

    public AppUser toEntityWithRole(@Valid AppUserWithRoleDto appUserDto) {
        return AppUser.builder()
                .email(appUserDto.email())
                .password(appUserDto.password())
                .role(appUserDto.role())
                .build();
    }

    public AppUserResponseDto toResponseDto(@Valid AppUser appUser) {
        return new AppUserResponseDto(
                appUser.getId(),
                appUser.getEmail(),
                appUser.getPortfolioItems()
        );
    }
}
