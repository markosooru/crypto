package com.prax.crypto.portfolio;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.prax.crypto.account.AppUser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Portfolio {

    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotEmpty
    private String currency;
    @NotNull
    @PastOrPresent(message = "Date of purchase must be in the past or present")
    private LocalDateTime dateOfPurchase;

    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    @JsonBackReference
    private AppUser appUser;
}
