package com.example.payment_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment_service.exception.ExpiredCreditCardException;
import com.example.payment_service.exception.InvalidCreditCardException;
import com.example.payment_service.exception.UnacceptedCreditCardException;
import com.example.payment_service.model.ChargeRequest;
import com.example.payment_service.model.ChargeResponse;
import com.example.payment_service.service.PaymentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> getCharge(@RequestBody ChargeRequest request) {
        
        logger.info("Request received: GET /api/charge");

        try{
            ChargeResponse response = paymentService.charge(request.getAmount(), request.getCreditCard());

            logger.info("Payment charge processed successfully");

            return ResponseEntity.ok(response);

        } catch (ExpiredCreditCardException e) {
            logger.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        
        } catch (InvalidCreditCardException e) {
            logger.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        
        } catch (UnacceptedCreditCardException e) {
            logger.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        
        } catch (Exception e) {
            logger.error("Internal error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}