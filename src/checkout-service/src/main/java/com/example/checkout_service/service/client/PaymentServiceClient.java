package com.example.checkout_service.service.client;

import com.example.checkout_service.dto.PaymentServiceCardDto;
import com.example.checkout_service.exception.ExternalServiceException;
import com.example.checkout_service.exception.PaymentProcessingException;
import com.example.checkout_service.model.CreditCardInfo;
import com.example.checkout_service.model.Money;
import com.example.checkout_service.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceClient implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClient.class);
    private static final String SERVICE_NAME = "Payment";

    private final RestTemplate restTemplate;
    private final String paymentServiceUrl;

    public PaymentServiceClient(RestTemplate restTemplate, @Value("${services.payment.url}") String paymentServiceUrl) {
        this.restTemplate = restTemplate;
        this.paymentServiceUrl = paymentServiceUrl;
    }

    @Override
    public String chargeCard(Money amount, CreditCardInfo creditCard) {
        logger.info("Charging card for amount: {} {}", amount.getUnits(), amount.getCurrencyCode());
        
        // Validate input before sending
        if (amount == null) {
            throw new PaymentProcessingException("Payment amount cannot be null");
        }
        
        if (creditCard == null) {
            throw new PaymentProcessingException("Credit card information cannot be null");
        }

        PaymentServiceCardDto paymentCard = convertToPaymentServiceFormat(creditCard);

        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("amount", amount);
        requestMap.put("creditCard", paymentCard);
        
        try {
            Map<String, String> response = restTemplate.postForObject(
                    paymentServiceUrl + "/api/v1/charge",
                    requestMap,
                    Map.class);
                    
            if (response == null || !response.containsKey("transactionId")) {
                logger.error("Payment service returned an invalid response: {}", response);
                throw new ExternalServiceException(SERVICE_NAME, "Payment service returned an invalid response");
            }
            
            String transactionId = response.get("transactionId");
            logger.info("Successfully processed payment with transaction ID: {}", transactionId);
            return transactionId;
            
        } catch (HttpClientErrorException ex) {
            // 4xx hataları - genellikle istemci hatası (yani bizim hatamız)
            logger.error("Payment service client error ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            
            // Kredi kartı hatası durumunda
            if (ex.getResponseBodyAsString().contains("Credit Card is not valid")) {
                throw new PaymentProcessingException("The provided credit card information is invalid");
            } 
            // Yetersiz bakiye durumunda
            else if (ex.getResponseBodyAsString().contains("Insufficient funds")) {
                throw new PaymentProcessingException("Insufficient funds for payment");
            }
            // Diğer client hataları
            else {
                throw new PaymentProcessingException("Payment processing failed: " + ex.getMessage());
            }
            
        } catch (HttpServerErrorException ex) {
            // 5xx hataları - genellikle sunucu hatası
            logger.error("Payment service server error ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ExternalServiceException(SERVICE_NAME, "Payment service unavailable or returned an error: " + ex.getMessage());
            
        } catch (ResourceAccessException ex) {
            // Bağlantı hatası
            logger.error("Cannot connect to payment service", ex);
            throw new ExternalServiceException(SERVICE_NAME, "Cannot connect to payment service: " + ex.getMessage());
            
        } catch (Exception ex) {
            // Diğer tüm hatalar
            logger.error("Unexpected error during payment processing", ex);
            throw new ExternalServiceException(SERVICE_NAME, "Unexpected error during payment processing: " + ex.getMessage());
        }
    }

    private PaymentServiceCardDto convertToPaymentServiceFormat(CreditCardInfo creditCard) {
        PaymentServiceCardDto paymentCard = new PaymentServiceCardDto();
        
        // Convert number field to cardNo
        paymentCard.setCardNo(creditCard.getCreditCardNumber());
        
        // Convert cvv from int to String
        paymentCard.setCvv(String.valueOf(creditCard.getCreditCardCvv()));
        
        // Format expirationMonth and expirationYear into MM/YYYY format
        String expirationDate = String.format("%02d/%d", 
                creditCard.getCreditCardExpirationMonth(),
                creditCard.getCreditCardExpirationYear());
        paymentCard.setExpirationDate(expirationDate);
        
        return paymentCard;
    }
}