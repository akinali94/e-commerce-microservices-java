package com.example.checkout_service.service.client;

import com.example.checkout_service.dto.CurrencyConversionResponse;
import com.example.checkout_service.model.Money;
import com.example.checkout_service.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;

@Service
public class CurrencyServiceClient implements CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceClient.class);

    private final RestTemplate restTemplate;
    private final String currencyServiceUrl;

    public CurrencyServiceClient(RestTemplate restTemplate, @Value("${services.currency.url}") String currencyServiceUrl) {
        this.restTemplate = restTemplate;
        this.currencyServiceUrl = currencyServiceUrl;
    }

    @Override
    public Money convertCurrency(Money from, String toCurrency) {
        String fromCurrency = from.getCurrencyCode();
        // Calculate total amount in dollars and cents
        double amount = from.getUnits() + (from.getNanos() / 1_000_000_000.0);

        logger.info("Converting {} {} to {}", amount, fromCurrency, toCurrency);

        String url = UriComponentsBuilder
                .fromUriString(currencyServiceUrl + "/api/v1/convert")
                .queryParam("from", fromCurrency)
                .queryParam("to", toCurrency)
                .queryParam("amount", amount)
                .toUriString();

        ResponseEntity<CurrencyConversionResponse> response = restTemplate.getForEntity(url, CurrencyConversionResponse.class);
        
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.error("Failed to convert currency from {} {} to {}", amount, fromCurrency, toCurrency);
            throw new RuntimeException("Currency conversion failed");
        }
        
        double convertedAmount = response.getBody().getResult();
        
        // Convert to Money format (units and nanos)
        long units = (long) convertedAmount;
        int nanos = (int) ((convertedAmount - units) * 1_000_000_000);
        
        logger.info("Conversion result: {} {} = {} {}", amount, fromCurrency, convertedAmount, toCurrency);
        
        return new Money(units, nanos, toCurrency);
    }
}