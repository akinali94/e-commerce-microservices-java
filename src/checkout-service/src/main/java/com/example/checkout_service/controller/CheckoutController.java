package com.example.checkout_service.controller;

import com.example.checkout_service.dto.PlaceOrderRequest;
import com.example.checkout_service.dto.PlaceOrderResponse;
import com.example.checkout_service.service.CheckoutService;
import com.example.checkout_service.util.LoggingUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for checkout operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    
    private final CheckoutService checkoutService;
    
    /**
     * Places an order.
     * 
     * POST /api/checkout/place-order
     * 
     * @param request the place order request
     * @return the place order response
     */
    @PostMapping("/place-order")
    public ResponseEntity<PlaceOrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request) {
        
        // Generate request ID for tracing
        String requestId = LoggingUtil.generateAndSetRequestId();
        
        log.info("Received place order request. RequestId: {}, UserId: {}", 
                requestId, request.getUserId());
        
        try {
            PlaceOrderResponse response = checkoutService.placeOrder(request);
            
            log.info("Place order request completed successfully. RequestId: {}, OrderId: {}", 
                    requestId, response.getOrder().getOrderId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } finally {
            LoggingUtil.clearMDC();
        }
    }
    
    /**
     * Health check endpoint.
     * 
     * GET /api/checkout/health
     * 
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("SERVING", "checkout-service"));
    }
    
    /**
     * Health response DTO.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class HealthResponse {
        private String status;
        private String service;
    }
}