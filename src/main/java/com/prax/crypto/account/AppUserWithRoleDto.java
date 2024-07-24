package com.prax.crypto.account;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        @Enumerated(EnumType.STRING)
        Role role
) {
}
