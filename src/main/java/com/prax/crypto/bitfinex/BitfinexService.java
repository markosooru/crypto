package com.prax.crypto.bitfinex;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BitfinexService {

    private final RestClient restClient;

    @Value("${bitfinex.fx-url}")
    private String fxUrl;

    public BigDecimal getCryptoFxInEur(String cryptoCurrency) {
        String payload = "{\"ccy1\":\"" + cryptoCurrency + "\",\"ccy2\":\"USD\"}";

        try {
            BigDecimal[] fxRate = restClient.post()
                    .uri(fxUrl)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(payload)
                    .retrieve()
                    .body(BigDecimal[].class);

            if (fxRate != null && fxRate.length == 1) {
                return fxRate[0];
            } else {
                throw new RestClientException("Invalid response received from the API");
            }

        } catch (RestClientException e) {
            throw new RestClientException("Failed to fetch fx data", e);
        }
    }
}