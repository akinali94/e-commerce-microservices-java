package com.example.shipping_service.dto;

public class ShipOrderResponse {
    private String trackingId;
    
    public ShipOrderResponse() {}
    
    public ShipOrderResponse(String trackingId) {
        this.trackingId = trackingId;
    }
    
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
}