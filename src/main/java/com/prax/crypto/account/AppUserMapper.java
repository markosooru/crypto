package com.prax.crypto.account;

import com.prax.crypto.account.dto.AppUserDto;
import com.prax.crypto.account.dto.AppUserResponseDto;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import org.springframework.stereotype.Service;

@Service
public class AppUserMapper {

    public AppUser toEntity(AppUserDto appUserDto) {
        return AppUser.builder()
                .email(appUserDto.email())
                .password(appUserDto.password())
                .role(Role.ROLE_USER)
                .build();
    }

    public AppUser toEntityWithRole(AppUserWithRoleDto appUserDto) {
        return AppUser.builder()
                .email(appUserDto.email())
                .password(appUserDto.password())
                .role(appUserDto.role())
                .build();
    }

    public AppUserResponseDto toResponseDto(AppUser appUser) {
        return new AppUserResponseDto(
                appUser.getId(),
                appUser.getEmail(),
                appUser.getPortfolioItems()
        );
    }
}
