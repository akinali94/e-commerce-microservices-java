package com.example.checkout_service.controller;

import com.example.checkout_service.dto.PlaceOrderRequest;
import com.example.checkout_service.dto.PlaceOrderResponse;
import com.example.checkout_service.service.CheckoutService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /**
     * GET /
     * API information endpoint
     * 
     * @return API information
     */
    @GetMapping("/checkout")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Checkout Service API");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/checkout/", "API information");
        endpoints.put("GET /api/checkout/health", "Health check");
        endpoints.put("POST /api/checkout/orders", "Place an order");
        
        info.put("endpoints", endpoints);
        
        Map<String, String> dependencies = new HashMap<>();
        dependencies.put("Cart Service", "Gets user cart items");
        dependencies.put("Product Service", "Gets product information");
        dependencies.put("Currency Service", "Handles currency conversion");
        dependencies.put("Shipping Service", "Calculates shipping costs and ships orders");
        dependencies.put("Payment Service", "Processes payments");
        dependencies.put("Email Service", "Sends order confirmations");
        
        info.put("dependencies", dependencies);
        
        return ResponseEntity.ok(info);
    }

    @PostMapping("/orders")
    public ResponseEntity<PlaceOrderResponse> placeOrder(@Validated @RequestBody PlaceOrderRequest request) {
        logger.info("Received order request for user: {}", request.getUserId());
        
        try {
            PlaceOrderResponse response = checkoutService.placeOrder(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Checkout Service is healthy");
    }
}