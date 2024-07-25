package com.prax.crypto.account;

import com.prax.crypto.account.dto.AppUserDto;
import com.prax.crypto.account.dto.AppUserResponseDto;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    public AppUserResponseDto create(@RequestBody @Valid AppUserDto user) {
        return appUserService.create(user);
    }

    @PostMapping("/withrole")
    @PreAuthorize("hasRole('ADMIN')")
    public AppUserResponseDto createWithRole(@RequestBody @Valid AppUserWithRoleDto user) {
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
    public AppUserResponseDto update(@PathVariable Integer id, @RequestBody @Valid AppUserDto user) {
        return this.appUserService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@appUserService.hasPermission(#id)")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        this.appUserService.delete(id);
    }
}