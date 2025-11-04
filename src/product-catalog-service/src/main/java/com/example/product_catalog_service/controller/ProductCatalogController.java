package com.example.product_catalog_service.controller;

import com.example.product_catalog_service.dto.ListProductsResponse;
import com.example.product_catalog_service.dto.SearchProductsResponse;
import com.example.product_catalog_service.exception.ProductNotFoundException;
import com.example.product_catalog_service.model.Product;
import com.example.product_catalog_service.service.ProductCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProductCatalogController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogController.class);
    
    private final ProductCatalogService productCatalogService;
    
    public ProductCatalogController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    /**
     * GET /api
     * API information endpoint
     */
    @GetMapping("/productcatalogservice")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Product Catalog Service API");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/v1/productcatalogservice", "API information");
        endpoints.put("GET /api/v1/health", "Health check");
        endpoints.put("GET /api/v1/products", "List all products");
        endpoints.put("GET /api/v1/products/{id}", "Get product by ID");
        endpoints.put("POST /api/v1/products/batch", "Get multiple products by IDs");
        endpoints.put("GET /api/v1/products/search", "Search products (query: q)");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * GET /api/products
     * List all products
     */
    @GetMapping("/products")
    public ResponseEntity<ListProductsResponse> listProducts() throws IOException {
        ListProductsResponse response = productCatalogService.listProducts();
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/products/{id}
     * Get a single product by ID
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) throws IOException {
        Product product = productCatalogService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    /**
     * POST /api/v1/products/batch
     * Get multiple products by their IDs
     */
    @PostMapping("/products/batch")
    public ResponseEntity<ListProductsResponse> getProductsByIds(@RequestBody List<String> ids) throws IOException {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Product IDs list cannot be empty");
        }
        
        if (ids.size() > 20) {
            throw new IllegalArgumentException("Cannot request more than 20 products at once");
        }
        
        logger.info("Batch product request for {} IDs", ids.size());
        ListProductsResponse response = productCatalogService.getProductsById(ids);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/products/search?q=query
     * Search products by query string
     */
    @GetMapping("/products/search")
    public ResponseEntity<SearchProductsResponse> searchProducts(@RequestParam(value = "q", required = true) String query) throws IOException {
        if (query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query parameter 'q' cannot be empty");
        }
        
        SearchProductsResponse response = productCatalogService.searchProducts(query);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "SERVING");
        health.put("service", "productcatalogservice");
        return ResponseEntity.ok(health);
    }
    
    /**
     * Exception handler for ProductNotFoundException
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound(ProductNotFoundException ex) {
        logger.warn("Product not found: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Product not found");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Exception handler for IOException
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException ex) {
        logger.error("IO error occurred", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("message", "An error occurred while processing your request");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Exception handler for IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid request: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad request");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Global exception handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("message", "An unexpected error occurred");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}