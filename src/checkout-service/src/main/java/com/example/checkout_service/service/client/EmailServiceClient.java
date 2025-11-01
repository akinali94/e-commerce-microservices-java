package com.example.checkout_service.service.client;

import com.example.checkout_service.model.OrderResult;
import com.example.checkout_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceClient implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceClient.class);

    private final RestTemplate restTemplate;
    private final String emailServiceUrl;

    public EmailServiceClient(RestTemplate restTemplate, @Value("${services.email.url}") String emailServiceUrl) {
        this.restTemplate = restTemplate;
        this.emailServiceUrl = emailServiceUrl;
    }

    @Override
    public void sendOrderConfirmation(String email, OrderResult order) {
        logger.info("Sending order confirmation email to: {}", email);
        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("email", email);
        requestMap.put("order", order);
        
        restTemplate.postForObject(
                emailServiceUrl + "/send-order-confirmation",
                requestMap,
                Void.class);
    }
}