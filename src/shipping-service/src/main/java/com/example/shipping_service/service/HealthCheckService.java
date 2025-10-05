package com.example.shipping_service.service;

import com.example.shipping_service.dto.HealthCheckResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HealthCheckService {
    

    public HealthCheckResponse checkHealth() {
        HealthCheckResponse response = new HealthCheckResponse();
        response.setStatus("SERVING");
        response.setTimestamp(Instant.now());
        
        return response;
    }
}