package com.prax.crypto.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
}
