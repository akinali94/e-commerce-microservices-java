package com.example.email_service.controller;

import com.example.email_service.dto.SendOrderConfirmationRequest;
import com.example.email_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-order-confirmation")
    public ResponseEntity<String> sendOrderConfirmation(@Valid @RequestBody SendOrderConfirmationRequest request) {
        logger.info("Received request to send order confirmation email to: {}", request.getEmail());
        
        // async e-mail
        emailService.sendOrderConfirmation(request);
        
        // HTTP 202 ACCEPTED - async
        return new ResponseEntity<>("Email sending process has been initiated", HttpStatus.ACCEPTED);
    }
}