package com.prax.crypto.bitfinex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@Service
public class BitfinexService {

    private final WebClient webClient;

    public BitfinexService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public BigDecimal getCurrentPriceInEUR(String crypto) {
        String apiUrl = String.format("https://api-pub.bitfinex.com/v2/ticker/t%sEUR", crypto);

        Mono<String> response = webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class);

        String jsonResponse = response.block();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            return root.get(6).decimalValue();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}