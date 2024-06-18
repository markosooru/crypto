package com.prax.crypto.portfolioitem;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.prax.crypto.appuser.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public record PortfolioItem(
        @Id
        @GeneratedValue
        Integer id,
        @Positive
        @NotEmpty
        BigDecimal amount,
        @NotEmpty
        String currency,
        @NotEmpty
        LocalDateTime dateOfPurchase,

        @ManyToOne
        @JoinColumn(name = "app_user_id")
        @JsonBackReference
        AppUser appUser
) {
}
