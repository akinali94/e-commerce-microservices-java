package com.example.currency_service.model;

import java.time.LocalDateTime;

public class HealthResponse {
    private String status;
    private LocalDateTime timestamp;
    private String service;

    public HealthResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public HealthResponse(String status, String service) {
        this.status = status;
        this.service = service;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
