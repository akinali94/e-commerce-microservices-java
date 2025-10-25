package com.example.email_service.service;

import com.example.email_service.dto.SendOrderConfirmationRequest;

import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

public interface EmailService {
    /**
     * Sends an order confirmation email to the customer
     * 
     * @param request The request containing email address and order details
     * @return CompletableFuture<Boolean> that completes with true if email was sent successfully, false otherwise
     */
    CompletableFuture<Boolean> sendOrderConfirmation(@Valid SendOrderConfirmationRequest request);
}