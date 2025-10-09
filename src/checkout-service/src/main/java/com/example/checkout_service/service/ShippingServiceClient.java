package com.example.checkout_service.service;

import com.example.checkoutservice.dto.ExternalServiceDtos;
import com.example.checkoutservice.exception.ExternalServiceException;
import com.example.checkoutservice.exception.ShippingFailedException;
import com.example.checkoutservice.model.Address;
import com.example.checkoutservice.model.CartItem;
import com.example.checkoutservice.model.Money;
import com.example.checkoutservice.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;

/**
 * Client for Shipping Service operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingServiceClient {
    
    @Qualifier("shippingServiceWebClient")
    private final WebClient webClient;
    
    private static final String SERVICE_NAME = "ShippingService";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    /**
     * Gets a shipping quote for the given address and items.
     * 
     * @param address the shipping address
     * @param items the cart items
     * @return the shipping cost in USD
     */
    public Money getQuote(Address address, List<CartItem> items) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "getQuote");
        
        ExternalServiceDtos.GetQuoteRequest request = ExternalServiceDtos.GetQuoteRequest.builder()
                .address(address)
                .items(items)
                .build();
        
        try {
            ExternalServiceDtos.GetQuoteResponse response = webClient.post()
                    .uri("/quote")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ExternalServiceDtos.GetQuoteResponse.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            if (response == null || response.getCostUsd() == null) {
                throw new ShippingFailedException("Shipping quote response is invalid");
            }
            
            log.debug("Shipping quote: {} USD", response.getCostUsd().getUnits());
            
            return response.getCostUsd();
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, 
                    "Failed to get shipping quote: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ExternalServiceException(SERVICE_NAME, 
                    "Failed to get shipping quote: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ships an order to the given address.
     * 
     * @param address the shipping address
     * @param items the cart items
     * @return the tracking ID
     */
    public String shipOrder(Address address, List<CartItem> items) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "shipOrder");
        
        ExternalServiceDtos.ShipOrderRequest request = ExternalServiceDtos.ShipOrderRequest.builder()
                .address(address)
                .items(items)
                .build();
        
        try {
            ExternalServiceDtos.ShipOrderResponse response = webClient.post()
                    .uri("/ship")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ExternalServiceDtos.ShipOrderResponse.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            if (response == null || response.getTrackingId() == null || response.getTrackingId().isEmpty()) {
                throw new ShippingFailedException("Ship order response is invalid - no tracking ID");
            }
            
            log.info("Order shipped successfully. Tracking ID: {}", response.getTrackingId());
            
            return response.getTrackingId();
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ShippingFailedException(
                    "Failed to ship order: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            throw new ShippingFailedException("Failed to ship order: " + e.getMessage(), e);
        }
    }
}