package com.prax.crypto.bitfinex;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;


@Service
public class BitfinexService {

    private final RestClient restClient;

    private static final String TICKER_URL = "https://api.bitfinex.com/v2/ticker/";

    public BitfinexService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public Ticker getTicker(String cryptoCurrency) {
        String symbol = "t" + cryptoCurrency + "EUR"; // Format the symbol as t{CRYPTO}EUR
        try {
            BigDecimal[] tickerData = restClient.get()
                    .uri(TICKER_URL + symbol)
                    .retrieve()
                    .body(BigDecimal[].class);

            if (tickerData != null && tickerData.length >= 10) {
                return new Ticker(
                        tickerData[0],  // bid
                        tickerData[1],  // bidSize
                        tickerData[2],  // ask
                        tickerData[3],  // askSize
                        tickerData[4],  // dailyChange
                        tickerData[5],  // dailyChangeRelative
                        tickerData[6],  // lastPrice
                        tickerData[7],  // volume
                        tickerData[8],  // high
                        tickerData[9]   // low
                );
            } else {
                throw new RestClientException("Invalid response from API");
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch ticker data", e);
        }
    }
}