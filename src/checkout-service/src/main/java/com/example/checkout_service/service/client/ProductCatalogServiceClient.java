package com.example.checkout_service.service.client;

import com.example.checkout_service.service.ProductCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductCatalogServiceClient implements ProductCatalogService {
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogServiceClient.class);

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductCatalogServiceClient(RestTemplate restTemplate, @Value("${services.product.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    public Product getProduct(String productId) {
        logger.info("Getting product: {}", productId);
        return restTemplate.getForObject(
                productServiceUrl + "/products/" + productId, 
                Product.class);
    }
}