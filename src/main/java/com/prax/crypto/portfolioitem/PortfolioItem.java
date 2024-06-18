package com.prax.crypto.portfolioitem;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.prax.crypto.appuser.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class PortfolioItem {

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

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    @JsonBackReference
    private AppUser appUser;

    public PortfolioItem() {
    }

    public PortfolioItem(BigDecimal amount, String currency, LocalDateTime dateOfPurchase, AppUser appUser) {
        this.amount = amount;
        this.currency = currency;
        this.dateOfPurchase = dateOfPurchase;
        this.appUser = appUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(LocalDateTime dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
