package org.alsception.pegasus.services;

import java.math.BigDecimal;
import org.alsception.pegasus.dto.HnbApiExchangeRateResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class HnbApiService {

    private final WebClient webClient;   
    
    public HnbApiService() {
        this.webClient = WebClient.create("https://api.hnb.hr");
    }
    
    public Mono<HnbApiExchangeRateResponse> fetchData() {
        return webClient.get()
            .uri("/tecajn-eur/v3?valuta=USD")
            .retrieve()
            .bodyToMono(HnbApiExchangeRateResponse[].class) // Convert JSON array to Java array
            .map(responseArray -> responseArray.length > 0 ? responseArray[0] : null)
            .onErrorResume(Exception.class, ex -> 
            {
                System.err.println("Error fetching exchange rate: " + ex.getMessage());
                // Return a default response instead of throwing an error
                HnbApiExchangeRateResponse defaultResponse = new HnbApiExchangeRateResponse();
                defaultResponse.setDatum_primjene("N/A");
                defaultResponse.setSrednji_tecaj("0.0"); // Handle as invalid rate
                return Mono.just(defaultResponse);
            });
    }
    
    public BigDecimal getUsdPrice() {
        return fetchData() // Returns Mono<HnbApiExchangeRateResponse>
            .map(response -> BigDecimal.valueOf(response.getSrednjiTecajDouble())) // Extract & convert
            .defaultIfEmpty(BigDecimal.ZERO) // If response is null, return 0
            .block(); // Convert Mono<BigDecimal> to BigDecimal (synchronous)
    }
}
