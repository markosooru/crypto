package com.prax.crypto.account.dto;

import com.prax.crypto.account.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AppUserWithRoleDto(
        @NotEmpty
        @Email
        String email,
        @NotEmpty
        String password,
        @NotNull
        Role role
) {
}
