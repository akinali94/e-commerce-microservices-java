package com.example.shipping_service.dto;

import java.time.Instant;

public class ErrorResponse {
    private String error;
    private String message;
    private Integer statusCode;
    private Instant timestamp;
    
    public ErrorResponse() {}
    
    public ErrorResponse(String error, String message, Integer statusCode, Instant timestamp) {
        this.error = error;
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = timestamp;
    }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}