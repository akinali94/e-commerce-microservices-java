package com.example.checkout_service.controller;

import com.example.checkout_service.dto.PlaceOrderRequest;
import com.example.checkout_service.dto.PlaceOrderResponse;
import com.example.checkout_service.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
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