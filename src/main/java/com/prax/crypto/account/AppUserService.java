package com.prax.crypto.account;

import com.prax.crypto.account.dto.AppUserDto;
import com.prax.crypto.account.dto.AppUserResponseDto;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AppUserResponseDto create(AppUserDto user) {
        var appUser = appUserMapper.toEntity(user);
        return appUserMapper.toResponseDto(appUserRepository.save(appUser));
    }

    public AppUserResponseDto createWithRole(AppUserWithRoleDto user) {
        var appUser = appUserMapper.toEntityWithRole(user);
        return appUserMapper.toResponseDto(appUserRepository.save(appUser));
    }

    public List<AppUserResponseDto> findAll() {
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toResponseDto)
                .toList();
    }

    public AppUserResponseDto findById(Integer id) {
        var appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return appUserMapper.toResponseDto(appUser);
    }

    public AppUserResponseDto update(Integer id, AppUserDto user) {
        var appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        appUser.setEmail(user.email());
        appUser.setPassword(user.password());
        return appUserMapper.toResponseDto(appUserRepository.save(appUser));
    }

    public void delete(Integer id) {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        appUserRepository.deleteById(id);
    }

    public AppUser getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt jwt) {
            var email = jwt.getSubject();
            return appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }
        throw new SecurityException("User not authenticated");
    }

    public boolean hasPermission(Integer userId) {
        var authenticatedUser = getAuthenticatedUser();
        return authenticatedUser.getRole() == Role.ROLE_ADMIN || authenticatedUser.getId().equals(userId);
    }
}
