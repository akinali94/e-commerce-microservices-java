package com.example.payment_service.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment_service.dto.ChargeRequest;
import com.example.payment_service.dto.ChargeResponse;
import com.example.payment_service.dto.HealthResponse;
import com.example.payment_service.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/paymentservice")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Payment Service API");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/v1/paymentservice", "API information");
        endpoints.put("GET /api/v1/health", "Health check");
        endpoints.put("POST /api/v1/charge", "Process payment charge");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> processCharge(@RequestBody ChargeRequest request) {
        logger.info("Payment charge request received");

        // Exceptions handles with GlobalExceptionHandler
        ChargeResponse response = paymentService.charge(request.getAmount(), request.getCreditCard());
        
        logger.info("Payment charge processed successfully, transaction ID: {}", response.getTransactionId());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        logger.debug("Health check request received");
        
        HealthResponse healthResponse = new HealthResponse("UP", "Payment Service");
        
        return ResponseEntity.ok(healthResponse);
    }
}