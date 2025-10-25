package com.example.email_service.controller;

import com.example.email_service.dto.SendOrderConfirmationRequest;
import com.example.email_service.service.EmailService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("service", "Email Service API");
        info.put("description", "Microservice for sending various types of emails");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("GET /", "API information");
        endpoints.put("POST /api/email/send-order-confirmation", "Send order confirmation email");
        endpoints.put("GET /health", "Health check (if actuator enabled)");
        
        
        info.put("available_endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }

    @PostMapping("/send-order-confirmation")
    public ResponseEntity<String> sendOrderConfirmation(@Valid @RequestBody SendOrderConfirmationRequest request) {
        logger.info("Received request to send order confirmation email to: {}", request.getEmail());
        
        // async e-mail
        emailService.sendOrderConfirmation(request);
        
        // HTTP 202 ACCEPTED - async
        return new ResponseEntity<>("Email sending process has been initiated", HttpStatus.ACCEPTED);
    }
}