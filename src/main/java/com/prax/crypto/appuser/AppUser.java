package com.prax.crypto.appuser;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.prax.crypto.portfolioitem.PortfolioItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
public record AppUser(
        @Id
        @GeneratedValue
        Integer id,
        @NotEmpty
        String username,
        @NotEmpty
        @Email
        @Column(unique = true)
        String email,

        @OneToMany(mappedBy = "appUser")
        @JsonManagedReference
        List<PortfolioItem> portfolioItems
) {
}
