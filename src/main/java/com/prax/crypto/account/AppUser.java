package com.prax.crypto.account;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.prax.crypto.portfolio.Portfolio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue
    private Integer id;
    @NotEmpty
    private String username;
    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "appUser")
    @JsonManagedReference
    private List<Portfolio> portfolioItems;
}
