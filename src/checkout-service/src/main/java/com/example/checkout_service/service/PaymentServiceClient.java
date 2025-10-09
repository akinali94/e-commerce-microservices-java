package com.example.checkout_service.service;

import com.example.checkoutservice.dto.ExternalServiceDtos;
import com.example.checkoutservice.exception.ExternalServiceException;
import com.example.checkoutservice.exception.PaymentFailedException;
import com.example.checkoutservice.model.CreditCardInfo;
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
 * Client for Payment Service operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceClient {
    
    @Qualifier("paymentServiceWebClient")
    private final WebClient webClient;
    
    private static final String SERVICE_NAME = "PaymentService";
    private static final Duration TIMEOUT = Duration.ofSeconds(10); // Longer timeout for payment
    
    /**
     * Charges a credit card for the specified amount.
     * 
     * @param amount the amount to charge
     * @param creditCard the credit card information
     * @return the transaction ID
     */
    public String chargeCard(Money amount, CreditCardInfo creditCard) {
        long startTime = System.currentTimeMillis();
        LoggingUtil.logExternalServiceCall(log, SERVICE_NAME, "chargeCard");
        
        // Mask credit card number for logging
        String maskedCard = maskCreditCard(creditCard.getCreditCardNumber());
        log.info("Attempting to charge card ending in {} for amount: {} {}", 
                maskedCard, amount.getUnits(), amount.getCurrencyCode());
        
        ExternalServiceDtos.ChargeRequest request = ExternalServiceDtos.ChargeRequest.builder()
                .amount(amount)
                .creditCard(creditCard)
                .build();
        
        try {
            ExternalServiceDtos.ChargeResponse response = webClient.post()
                    .uri("/charge")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ExternalServiceDtos.ChargeResponse.class)
                    .timeout(TIMEOUT)
                    .block();
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.logExternalServiceSuccess(log, SERVICE_NAME, duration);
            
            if (response == null || response.getTransactionId() == null || response.getTransactionId().isEmpty()) {
                throw new PaymentFailedException("Payment response is invalid - no transaction ID");
            }
            
            log.info("Payment successful. Transaction ID: {}", response.getTransactionId());
            
            return response.getTransactionId();
        } catch (WebClientResponseException e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            log.error("Payment failed for card ending in {}: {} - {}", 
                    maskedCard, e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentFailedException(
                    "Failed to charge card: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            LoggingUtil.logExternalServiceError(log, SERVICE_NAME, e.getMessage());
            log.error("Payment failed for card ending in {}: {}", maskedCard, e.getMessage());
            throw new PaymentFailedException("Failed to charge card: " + e.getMessage(), e);
        }
    }
    
    /**
     * Masks a credit card number for logging purposes.
     * Shows only the last 4 digits.
     * 
     * @param cardNumber the card number
     * @return masked card number
     */
    private String maskCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }
}