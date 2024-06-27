package com.prax.crypto.portfolio;

import com.prax.crypto.account.AppUserRepository;
import com.prax.crypto.bitfinex.BitfinexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortfolioMapperTest {

    private PortfolioMapper mapper;
    private AppUserRepository appUserRepository;
    private BitfinexService bitfinexService;

    @BeforeEach
    void setUp() {
        mapper = new PortfolioMapper(appUserRepository, bitfinexService);
    }

    @Test
    void toPortfolioResponseDto() {
        // given
        // var appUser = new AppUser("testman", ") ;
    }

    @Test
    void toPortfolio() {
    }
}