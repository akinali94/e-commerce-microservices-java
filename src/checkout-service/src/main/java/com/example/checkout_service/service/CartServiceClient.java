package com.example.checkout_service.service;

import com.example.checkout_service.dto.ExternalServiceDtos;
import com.example.checkout_service.exception.ExternalServiceException;
import com.example.checkout_service.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client for Cart Service operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceClient {
    
    @Qualifier("cartServiceWebClient")
    private final WebClient webClient;
    
    private static final String SERVICE_NAME = "CartService";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    /**
     * Retrieves the cart for a specific user.
     * 
     * @param userId the user ID
     * @return the cart response
     */
    public ExternalServiceDtos.GetCartResponse getCart(String userId) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "getCart");
        
        try {
            ExternalServiceDtos.GetCartResponse response = webClient.get()
                    .uri("/{userId}", userId)
                    .retrieve()
                    .bodyToMono(ExternalServiceDtos.GetCartResponse.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            return response;
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, 
                    "Failed to get cart: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, "Failed to get cart: " + e.getMessage(), e);
        }
    }
    
    /**
     * Empties the cart for a specific user.
     * 
     * @param userId the user ID
     */
    public void emptyCart(String userId) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "emptyCart");
        
        try {
            webClient.delete()
                    .uri("/{userId}", userId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            log.info("Successfully emptied cart for user: {}", userId);
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            log.warn("Failed to empty cart for user {}: {}", userId, e.getMessage());
            // Don't throw exception here as this is not critical for order completion
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            log.warn("Failed to empty cart for user {}: {}", userId, e.getMessage());
            // Don't throw exception here as this is not critical for order completion
        }
    }
}