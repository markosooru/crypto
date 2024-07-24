package com.prax.crypto.account;

import com.prax.crypto.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/appuser")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;
    private final TokenService tokenService;
    private final BearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();

    @PostMapping("/login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        var token = bearerTokenResolver.resolve(request);
        if (token != null) {
            tokenService.blacklistToken(token);
        SecurityContextHolder.clearContext();
        }
    }

    @PostMapping
    public AppUserResponseDto create(@RequestBody AppUserDto user) {
        return appUserService.create(user);
    }

    @PostMapping("/withrole")
    @PreAuthorize("hasRole('ADMIN')")
    public AppUserResponseDto createWithRole(@RequestBody AppUserWithRoleDto user) {
        return appUserService.createWithRole(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppUserResponseDto> findAll() {
        return appUserService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@appUserService.hasPermission(#id)")
    public AppUserResponseDto findById(@PathVariable Integer id) {
        return appUserService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@appUserService.hasPermission(#id)")
    public AppUserResponseDto update(@PathVariable Integer id, @RequestBody AppUserDto user) {
        return this.appUserService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@appUserService.hasPermission(#id)")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        this.appUserService.delete(id);
    }

    @GetMapping("/validateRoles")
    public String validateRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return "Authorities: " + authentication.getAuthorities();
        }
        return "No authentication";
    }
}