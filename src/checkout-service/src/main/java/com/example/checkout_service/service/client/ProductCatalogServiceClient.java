package com.example.checkout_service.service.client;

import com.example.checkout_service.dto.BatchProductsResponse;
import com.example.checkout_service.service.ProductCatalogService;
import com.example.checkout_service.model.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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
                productServiceUrl + "/api/v1/products/" + productId, 
                Product.class);
    }

    @Override
    public List<Product> getMultipleProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            logger.warn("Empty product ID list provided to getMultipleProducts");
            return new ArrayList<>();
        }
        
        logger.info("Getting multiple products: {}", productIds);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create the request entity with the product IDs list as the body
        HttpEntity<List<String>> requestEntity = new HttpEntity<>(productIds, headers);
        
        BatchProductsResponse response = restTemplate.postForObject(
                productServiceUrl + "/api/v1/products/batch",
                requestEntity,
                BatchProductsResponse.class);
        
        if (response == null || response.getProducts() == null) {
            logger.warn("No products returned from batch request");
            return new ArrayList<>();
        }
        
        logger.info("Retrieved {} products from batch request", response.getProducts().size());
        
        return response.getProducts();
    }
}