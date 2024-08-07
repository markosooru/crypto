package com.prax.crypto.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record AppUserDto(
        @NotEmpty
        @Email
        String email,
        @NotEmpty
        String password
) {
}
