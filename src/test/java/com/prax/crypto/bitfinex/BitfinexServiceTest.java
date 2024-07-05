package com.prax.crypto.bitfinex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void GetTicker_RetrievesDataAndMapsToTicker() {
        // given
        String TICKER_URL = "https://api.bitfinex.com/v2/ticker/tBTCEUR";
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

        // mock
        when(restClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(TICKER_URL)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        when(responseSpec.body(BigDecimal[].class)).thenReturn(tickerData);

        // when
        Ticker ticker = bitfinexService.getTicker("BTC");

        // then
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
    void GetTicker_InvalidResponseThrowsRestClientException() {
        // given
        BigDecimal[] invalidTickerData = {new BigDecimal("10000.0")};
        String TICKER_URL = "https://api.bitfinex.com/v2/ticker/tBTCEUR";

        // mock
        when(restClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(TICKER_URL)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        when(responseSpec.body(BigDecimal[].class)).thenReturn(invalidTickerData);

        // when & then
        assertThrows(RuntimeException.class, () -> bitfinexService.getTicker("BTC"));
    }

    @Test
    void GetTicker_ApiFailureThrowsRestClientException() {
        // given
        String TICKER_URL = "https://api.bitfinex.com/v2/ticker/yBTCEUR";

        // mock
        when(restClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(TICKER_URL)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        when(responseSpec.body(BigDecimal[].class)).thenThrow(new RestClientException("API failure"));

        // when & then
        assertThrows(RuntimeException.class, () -> bitfinexService.getTicker("BTC"));
    }
}