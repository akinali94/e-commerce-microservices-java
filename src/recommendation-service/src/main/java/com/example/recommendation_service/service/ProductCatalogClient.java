package com.example.recommendation_service.service;

import com.hipstershop.recommendationservice.model.ListRecommendationsRequest;
import com.hipstershop.recommendationservice.model.ListRecommendationsResponse;
import com.hipstershop.recommendationservice.model.Product;
import com.hipstershop.recommendationservice.model.ProductCatalogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductCatalogClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogClient.class);
    
    private final RestTemplate restTemplate;
    private final String productCatalogUrl;
    
    public ProductCatalogClient(
            RestTemplate restTemplate,
            @Value("${product.catalog.url}") String productCatalogUrl) {
        this.restTemplate = restTemplate;
        this.productCatalogUrl = productCatalogUrl;
    }
    
    /**
     * Fetches all products from the Product Catalog service
     */
    public List<Product> listProducts() {
        try {
            String url = productCatalogUrl + "/products";
            logger.info("Fetching products from: {}", url);
            
            ProductCatalogResponse response = restTemplate.getForObject(
                    url,
                    ProductCatalogResponse.class
            );
            
            if (response == null || response.getProducts() == null) {
                logger.warn("Product Catalog returned null response");
                return new ArrayList<>();
            }
            
            logger.info("Successfully fetched {} products", response.getProducts().size());
            return response.getProducts();
            
        } catch (Exception e) {
            logger.error("Failed to fetch products from Product Catalog", e);
            throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
        }
    }
}