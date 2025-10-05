package com.example.shipping_service.controller;

import com.example.shipping_service.dto.ShipOrderRequest;
import com.example.shipping_service.dto.ShipOrderResponse;
import com.example.shipping_service.dto.ShippingQuoteRequest;
import com.example.shipping_service.dto.ShippingQuoteResponse;
import com.example.shipping_service.service.ShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {
    
    private final ShippingService shippingService;
    
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    /**
     * POST /api/shipping/quote
     * Get shipping quote
     */
    @PostMapping("/quote")
    public ResponseEntity<ShippingQuoteResponse> getQuote(
            @Valid @RequestBody ShippingQuoteRequest request) {
        ShippingQuoteResponse response = shippingService.getQuote(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/shipping/ship
     * Ship order and get tracking ID
     */
    @PostMapping("/ship")
    public ResponseEntity<ShipOrderResponse> shipOrder(
            @Valid @RequestBody ShipOrderRequest request) {
        ShipOrderResponse response = shippingService.shipOrder(request);
        return ResponseEntity.ok(response);
    }
}