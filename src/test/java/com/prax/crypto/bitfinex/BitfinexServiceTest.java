package com.prax.crypto.bitfinex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BitfinexServiceTest {

    @InjectMocks
    private BitfinexService bitfinexService;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private static final String TICKER_URL = "https://api.bitfinex.com/v2/ticker/tBTCEUR";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTicker_Success() {
        BigDecimal[] tickerData = {
                new BigDecimal("10000.0"), // bid
                new BigDecimal("10.0"),    // bidSize
                new BigDecimal("10050.0"), // ask
                new BigDecimal("15.0"),    // askSize
                new BigDecimal("50.0"),    // dailyChange
                new BigDecimal("0.005"),   // dailyChangeRelative
                new BigDecimal("10025.0"), // lastPrice
                new BigDecimal("100000.0"),// volume
                new BigDecimal("10100.0"), // high
                new BigDecimal("9900.0")   // low
        };

        when(restClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(TICKER_URL)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        when(responseSpec.body(BigDecimal[].class)).thenReturn(tickerData);

        Ticker ticker = bitfinexService.getTicker("BTC");

        assertNotNull(ticker);
        assertEquals(tickerData[0], ticker.bid());
        assertEquals(tickerData[1], ticker.bidSize());
        assertEquals(tickerData[2], ticker.ask());
        assertEquals(tickerData[3], ticker.askSize());
        assertEquals(tickerData[4], ticker.dailyChange());
        assertEquals(tickerData[5], ticker.dailyChangeRelative());
        assertEquals(tickerData[6], ticker.lastPrice());
        assertEquals(tickerData[7], ticker.volume());
        assertEquals(tickerData[8], ticker.high());
        assertEquals(tickerData[9], ticker.low());
    }

    @Test
    void getTicker_InvalidResponse() {
        BigDecimal[] invalidTickerData = {new BigDecimal("10000.0")};

        when(restClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(TICKER_URL)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        when(responseSpec.body(BigDecimal[].class)).thenReturn(invalidTickerData);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> bitfinexService.getTicker("BTC"));
        assertInstanceOf(RestClientException.class, thrown.getCause());
    }

    @Test
    void getTicker_RestClientException() {
        when(restClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(TICKER_URL)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        when(responseSpec.body(BigDecimal[].class)).thenThrow(new RestClientException("API failure"));

        assertThrows(RuntimeException.class, () -> bitfinexService.getTicker("BTC"));
    }
}