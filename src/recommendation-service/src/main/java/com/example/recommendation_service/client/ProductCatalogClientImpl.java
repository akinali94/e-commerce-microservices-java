package com.example.recommendation_service.client;

import com.example.recommendation_service.dto.ListProductsResponse;
import com.example.recommendation_service.exception.ProductCatalogServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate-based implementation of the ProductCatalogClient.
 * Communicates with the Product Catalog Service via REST.
 */
@Component
public class ProductCatalogClientImpl implements ProductCatalogClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogClientImpl.class);
    
    private final RestTemplate restTemplate;
    private final String productCatalogServiceUrl;
    
    /**
     * Constructor with dependency injection.
     *
     * @param restTemplate The RestTemplate bean to use for HTTP requests
     * @param productCatalogServiceUrl The base URL of the product catalog service
     */
    public ProductCatalogClientImpl(
            RestTemplate restTemplate,
            @Value("${services.productcatalog.url}") String productCatalogServiceUrl) {
        this.restTemplate = restTemplate;
        this.productCatalogServiceUrl = productCatalogServiceUrl;
    }
    
    @Override
    public ListProductsResponse listProducts() {
        String url = productCatalogServiceUrl + "/api/v1/products";
        logger.info("Fetching products from {}", url);
        
        try {
            ListProductsResponse response = restTemplate.getForObject(url, ListProductsResponse.class);
            logger.info("Successfully fetched {} products from catalog service", 
                    response != null ? (response.getProducts() != null ? response.getProducts().size() : 0) : 0);
            return response;
        } catch (RestClientException e) {
            logger.error("Error fetching products from catalog service: {}", e.getMessage());
            throw new ProductCatalogServiceException("Failed to fetch products from catalog service", e);
        }
    }
}