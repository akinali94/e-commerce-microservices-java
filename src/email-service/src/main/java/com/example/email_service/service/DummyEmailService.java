package com.example.email_service.service;

import com.example.email_service.dto.SendOrderConfirmationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Service
@ConditionalOnProperty(name = "email.service.dummy-mode", havingValue = "true")
public class DummyEmailService implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(DummyEmailService.class);
    
    @Async
    @Override
    public CompletableFuture<Boolean> sendOrderConfirmation(@Valid SendOrderConfirmationRequest request) {
        logger.info("DUMMY MODE: A request to send order confirmation email to {} has been received.", request.getEmail());
        // In dummy mode, we just log the request without sending an actual email
        logger.info("DUMMY MODE: Simulating successful email sending to {}", request.getEmail());
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return CompletableFuture.completedFuture(true);
    }
}