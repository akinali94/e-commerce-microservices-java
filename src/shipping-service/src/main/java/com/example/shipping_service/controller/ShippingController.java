package com.example.shipping_service.controller;

import com.example.shipping_service.dto.ShipOrderRequest;
import com.example.shipping_service.dto.ShipOrderResponse;
import com.example.shipping_service.dto.ShippingQuoteRequest;
import com.example.shipping_service.dto.ShippingQuoteResponse;
import com.example.shipping_service.service.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ShippingController {
    
    private static final Logger logger = LoggerFactory.getLogger(ShippingController.class);
    
    private final ShippingService shippingService;
    
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    /**
     * GET /
     * API information endpoint
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Shipping Service API");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /", "API information");
        endpoints.put("GET /health", "Health check");
        endpoints.put("POST /api/shipping/quote", "Get shipping quote");
        endpoints.put("POST /api/shipping/ship", "Ship order and get tracking ID");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * GET /health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.info("Health check requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "shipping-service");
        response.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/shipping/quote
     * Get shipping quote
     */
    @PostMapping("/api/shipping/quote")
    public ResponseEntity<ShippingQuoteResponse> getQuote(
            @Valid @RequestBody ShippingQuoteRequest request) {
        logger.info("Received shipping quote request");
        ShippingQuoteResponse response = shippingService.getQuote(request);
        logger.info("Shipping quote generated successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/shipping/ship
     * Ship order and get tracking ID
     */
    @PostMapping("/api/shipping/ship")
    public ResponseEntity<ShipOrderResponse> shipOrder(
            @Valid @RequestBody ShipOrderRequest request) {
        logger.info("Received ship order request");
        ShipOrderResponse response = shippingService.shipOrder(request);
        logger.info("Order shipped successfully with tracking ID: {}", response.getTrackingId());
        return ResponseEntity.ok(response);
    }
}