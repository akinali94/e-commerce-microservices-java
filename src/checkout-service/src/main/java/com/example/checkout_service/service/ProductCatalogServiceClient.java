package com.example.checkout_service.service;

import com.example.checkoutservice.dto.ExternalServiceDtos;
import com.example.checkoutservice.exception.ExternalServiceException;
import com.example.checkoutservice.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Client for Product Catalog Service operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCatalogServiceClient {
    
    @Qualifier("productCatalogServiceWebClient")
    private final WebClient webClient;
    
    private static final String SERVICE_NAME = "ProductCatalogService";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    /**
     * Retrieves product details by product ID.
     * 
     * @param productId the product ID
     * @return the product details
     */
    public ExternalServiceDtos.Product getProduct(String productId) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "getProduct");
        
        try {
            ExternalServiceDtos.Product response = webClient.get()
                    .uri("/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(ExternalServiceDtos.Product.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            log.debug("Retrieved product: {} - {}", productId, response.getName());
            
            return response;
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, 
                    "Failed to get product " + productId + ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, 
                    "Failed to get product " + productId + ": " + e.getMessage(), e);
        }
    }
}