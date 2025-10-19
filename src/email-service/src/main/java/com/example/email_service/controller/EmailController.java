package com.example.email_service.controller;

import com.example.email_service.dto.SendOrderConfirmationRequest;
import com.example.email_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-order-confirmation")
    public ResponseEntity<Void> sendOrderConfirmation(@RequestBody SendOrderConfirmationRequest request) {
        emailService.sendOrderConfirmation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
```

### 11. Let's add a basic application.properties file:
```
# Server configuration
server.port=8080

# Application name
spring.application.name=email-service

# Logging configuration
logging.level.com.example.emailservice=INFO