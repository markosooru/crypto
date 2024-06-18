package com.prax.crypto.appuser;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.prax.crypto.portfolioitem.PortfolioItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
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
    private List<PortfolioItem> portfolioItems;

    public AppUser() {
    }

    public AppUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotEmpty String getUsername() {
        return username;
    }

    public void setUsername(@NotEmpty String username) {
        this.username = username;
    }

    public @NotEmpty @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotEmpty @Email String email) {
        this.email = email;
    }

    public List<PortfolioItem> getPortfolioItems() {
        return portfolioItems;
    }

    public void setPortfolioItems(List<PortfolioItem> portfolioItems) {
        this.portfolioItems = portfolioItems;
    }
}
