package com.example.checkout_service.service.client;

import com.example.checkout_service.model.Address;
import com.example.checkout_service.model.CartItem;
import com.example.checkout_service.model.Money;
import com.example.checkout_service.service.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceClient implements ShippingService {
    private static final Logger logger = LoggerFactory.getLogger(ShippingServiceClient.class);

    private final RestTemplate restTemplate;
    private final String shippingServiceUrl;

    public ShippingServiceClient(RestTemplate restTemplate, @Value("${services.shipping.url}") String shippingServiceUrl) {
        this.restTemplate = restTemplate;
        this.shippingServiceUrl = shippingServiceUrl;
    }

    @Override
    public Money getShippingQuote(Address address, List<CartItem> items) {
        logger.info("Getting shipping quote for address: {} and {} items", address, items.size());
        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("address", address);
        requestMap.put("items", items);
        
        return restTemplate.postForObject(
                shippingServiceUrl + "/shipping-quote",
                requestMap,
                Money.class);
    }

    @Override
    public String shipOrder(Address address, List<CartItem> items) {
        logger.info("Shipping order to address: {} with {} items", address, items.size());
        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("address", address);
        requestMap.put("items", items);
        
        Map<String, String> response = restTemplate.postForObject(
                shippingServiceUrl + "/ship-order",
                requestMap,
                Map.class);
                
        return response.get("trackingId");
    }
}