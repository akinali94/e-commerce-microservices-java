package com.example.shipping_service.controller;

import com.example.shipping_service.dto.HealthCheckResponse;
import com.example.shipping_service.service.HealthCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    
    private final HealthCheckService healthCheckService;
    
    public HealthController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }
    
    /**
     * GET /api/health
     * Root health check endpoint
     */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> rootHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "shipping-service");
        response.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/shipping/health
     * Shipping service health check
     */
    @GetMapping("/api/shipping/health")
    public ResponseEntity<HealthCheckResponse> shippingHealth() {
        HealthCheckResponse response = healthCheckService.checkHealth();
        return ResponseEntity.ok(response);
    }
}