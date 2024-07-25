package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    @Query("SELECT p FROM Portfolio p WHERE p.isDeleted = false")
    List<Portfolio> findAllActive();

    @Query("SELECT p FROM Portfolio p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Portfolio> findActiveById(@Param("id") Integer id);

    @Query("SELECT p FROM Portfolio p WHERE p.appUser = :appUser AND p.isDeleted = false")
    List<Portfolio> findAllActiveByAppUser(@Param("appUser") AppUser appUser);

    @Query("SELECT p FROM Portfolio p WHERE p.id = :id AND p.appUser = :appUser AND p.isDeleted = false")
    Optional<Portfolio> findActiveByIdAndAppUser(@Param("id") Integer id, @Param("appUser") AppUser appUser);
}
