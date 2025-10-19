package com.example.email_service.service;

import com.example.email_service.dto.SendOrderConfirmationRequest;
import com.example.email_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "email.service.dummy-mode", havingValue = "true")
public class DummyEmailService implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(DummyEmailService.class);
    
    @Override
    public void sendOrderConfirmation(SendOrderConfirmationRequest request) {
        logger.info("A request to send order confirmation email to {} has been received.", request.getEmail());
        // In dummy mode, we just log the request without sending an actual email
    }
}