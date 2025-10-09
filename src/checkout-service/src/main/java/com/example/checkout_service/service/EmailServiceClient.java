package com.example.checkout_service.service;

import com.example.checkoutservice.dto.ExternalServiceDtos;
import com.example.checkoutservice.exception.ExternalServiceException;
import com.example.checkoutservice.model.OrderResult;
import com.example.checkoutservice.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Client for Email Service operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceClient {
    
    @Qualifier("emailServiceWebClient")
    private final WebClient webClient;
    
    private static final String SERVICE_NAME = "EmailService";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    /**
     * Sends an order confirmation email.
     * 
     * @param email the recipient email address
     * @param order the order details
     */
    public void sendOrderConfirmation(String email, OrderResult order) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "sendOrderConfirmation");
        
        ExternalServiceDtos.SendOrderConfirmationRequest request = 
                ExternalServiceDtos.SendOrderConfirmationRequest.builder()
                        .email(email)
                        .order(order)
                        .build();
        
        try {
            webClient.post()
                    .uri("/send-confirmation")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            log.info("Order confirmation email sent successfully to: {}", email);
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            // Don't throw exception - email failure shouldn't fail the order
            log.warn("Failed to send order confirmation email to {}: {} - {}", 
                    email, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            // Don't throw exception - email failure shouldn't fail the order
            log.warn("Failed to send order confirmation email to {}: {}", email, e.getMessage());
        }
    }
}