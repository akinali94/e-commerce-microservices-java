package com.example.email_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    private final JavaMailSender emailSender;
    
    @Value("${spring.application.name:email-service}")
    private String applicationName;
    
    @Value("${email.service.dummy-mode:false}")
    private boolean dummyMode;

    public HealthCheckController(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> healthStatus = new LinkedHashMap<>();
        
        healthStatus.put("service", applicationName);
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("dummy_mode", dummyMode);
        details.put("mail_service", checkMailService());
        
        healthStatus.put("details", details);
        
        return ResponseEntity.ok(healthStatus);
    }
    
    private String checkMailService() {
        try {
            if (dummyMode) {
                return "SKIPPED (dummy mode enabled)";
            }
            
            if (emailSender != null) {
                return "UP";
            } else {
                return "DOWN (mail sender not available)";
            }
            
        } catch (Exception e) {
            return "DOWN (" + e.getMessage() + ")";
        }
    }
}