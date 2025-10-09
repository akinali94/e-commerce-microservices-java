package com.example.checkout_service.service;

import com.example.checkoutservice.dto.ExternalServiceDtos;
import com.example.checkoutservice.exception.ExternalServiceException;
import com.example.checkoutservice.model.Money;
import com.example.checkoutservice.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Client for Currency Service operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceClient {
    
    @Qualifier("currencyServiceWebClient")
    private final WebClient webClient;
    
    private static final String SERVICE_NAME = "CurrencyService";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    /**
     * Converts money from one currency to another.
     * 
     * @param from the source money
     * @param toCurrencyCode the target currency code
     * @return the converted money
     */
    public Money convertCurrency(Money from, String toCurrencyCode) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "convertCurrency");
        
        // If currencies are the same, no conversion needed
        if (from.getCurrencyCode().equals(toCurrencyCode)) {
            log.debug("No currency conversion needed: {} == {}", from.getCurrencyCode(), toCurrencyCode);
            return from;
        }
        
        try {
            Money response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/convert")
                            .queryParam("from_currency", from.getCurrencyCode())
                            .queryParam("from_units", from.getUnits())
                            .queryParam("from_nanos", from.getNanos())
                            .queryParam("to_code", toCurrencyCode)
                            .build())
                    .retrieve()
                    .bodyToMono(Money.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            log.debug("Converted {} {} to {} {}", 
                    from.getUnits(), from.getCurrencyCode(), 
                    response.getUnits(), response.getCurrencyCode());
            
            return response;
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, 
                    "Failed to convert currency from " + from.getCurrencyCode() + " to " + toCurrencyCode + 
                    ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, 
                    "Failed to convert currency from " + from.getCurrencyCode() + " to " + toCurrencyCode + 
                    ": " + e.getMessage(), e);
        }
    }
}