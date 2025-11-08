package com.example.checkout_service.service.client;

import com.example.checkout_service.exception.ExternalServiceException;
import com.example.checkout_service.model.OrderResult;
import com.example.checkout_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceClient implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceClient.class);
    private static final String SERVICE_NAME = "Email";

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
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);
        
        try {
            // exchange() kullanarak String tipinde yanıt almak
            ResponseEntity<String> response = restTemplate.exchange(
                    emailServiceUrl + "/api/v1/send-order-confirmation",
                    HttpMethod.POST,
                    entity,
                    String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to send email: {}", response.getBody());
                throw new ExternalServiceException(SERVICE_NAME, "Email service returned error: " + response.getBody());
            }
            
            logger.info("Email sent successfully: {}", response.getBody());
            
        } catch (HttpStatusCodeException ex) {
            logger.error("Email service error ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ExternalServiceException(SERVICE_NAME, "Email service error: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Failed to send email", ex);
            throw new ExternalServiceException(SERVICE_NAME, ex.getMessage());
        }
    }
}