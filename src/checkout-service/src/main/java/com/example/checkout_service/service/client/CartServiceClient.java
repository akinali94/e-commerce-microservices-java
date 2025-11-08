package com.example.checkout_service.service.client;

import com.example.checkout_service.model.CartItem;
import com.example.checkout_service.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CartServiceClient implements CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartServiceClient.class);

    private final RestTemplate restTemplate;
    private final String cartServiceUrl;

    public CartServiceClient(RestTemplate restTemplate, @Value("${services.cart.url}") String cartServiceUrl) {
        this.restTemplate = restTemplate;
        this.cartServiceUrl = cartServiceUrl;
    }

    @Override
    public List<CartItem> getCart(String userId) {
        logger.info("Getting cart for user: {}", userId);
        
        ResponseEntity<List<CartItem>> response = restTemplate.exchange(
                cartServiceUrl + "/api/v1/carts/" + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CartItem>>() {});
        
        return response.getBody();
    }

    @Override
    public void emptyCart(String userId) {
        logger.info("Emptying cart for user: {}", userId);
        restTemplate.delete(cartServiceUrl + "api/v1/carts/" + userId);
    }
}