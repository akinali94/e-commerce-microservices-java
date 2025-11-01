package com.example.checkout_service.service;

import com.example.checkout_service.model.OrderResult;

public interface EmailService {
    void sendOrderConfirmation(String email, OrderResult order);
}