package com.example.checkout_service.service.client;

import com.example.checkout_service.model.CreditCardInfo;
import com.example.checkout_service.model.Money;
import com.example.checkout_service.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceClient implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClient.class);

    private final RestTemplate restTemplate;
    private final String paymentServiceUrl;

    public PaymentServiceClient(RestTemplate restTemplate, @Value("${services.payment.url}") String paymentServiceUrl) {
        this.restTemplate = restTemplate;
        this.paymentServiceUrl = paymentServiceUrl;
    }

    @Override
    public String chargeCard(Money amount, CreditCardInfo creditCard) {
        logger.info("Charging card for amount: {} {}", amount.getUnits(), amount.getCurrencyCode());
        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("amount", amount);
        requestMap.put("creditCard", creditCard);
        
        Map<String, String> response = restTemplate.postForObject(
                paymentServiceUrl + "/charge",
                requestMap,
                Map.class);
                
        return response.get("transactionId");
    }
}