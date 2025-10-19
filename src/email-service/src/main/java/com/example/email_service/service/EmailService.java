package com.example.email_service.service;

import com.example.emailservice.dto.SendOrderConfirmationRequest;

public interface EmailService {
    void sendOrderConfirmation(SendOrderConfirmationRequest request);
}