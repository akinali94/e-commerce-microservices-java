package com.example.product_catalog_service.controller;

import com.example.product_catalog_service.dto.ListProductsResponse;
import com.example.product_catalog_service.dto.SearchProductsResponse;
import com.example.product_catalog_service.exception.ProductNotFoundException;
import com.example.product_catalog_service.model.Product;
import com.example.product_catalog_service.service.ProductCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductCatalogController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogController.class);
    
    private final ProductCatalogService productCatalogService;
    
    @Value("${extra.latency:0}")
    private long extraLatency;
    
    public ProductCatalogController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }
    
    /**
     * GET /api/products
     * List all products
     */
    @GetMapping("/products")
    public ResponseEntity<ListProductsResponse> listProducts() throws IOException, InterruptedException {
        applyExtraLatency();
        
        ListProductsResponse response = productCatalogService.listProducts();
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/products/{id}
     * Get a single product by ID
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) 
            throws IOException, InterruptedException {
        applyExtraLatency();
        
        Product product = productCatalogService.getProduct(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * GET /api/products/search?q=query
     * Search products by query string
     */
    @GetMapping("/products/search")
    public ResponseEntity<?> searchProducts(@RequestParam(value = "q", required = false) String query) 
            throws IOException, InterruptedException {
        applyExtraLatency();
        
        if (query == null || query.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Missing query parameter");
            error.put("message", "Query parameter 'q' is required");
            return ResponseEntity.badRequest().body(error);
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
     * Apply artificial latency for testing/demo purposes
     */
    private void applyExtraLatency() throws InterruptedException {
        if (extraLatency > 0) {
            Thread.sleep(extraLatency);
        }
    }
    
    /**
     * Exception handler for ProductNotFoundException
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound(ProductNotFoundException ex) {
        logger.error("Product not found: {}", ex.getMessage());
        
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
        logger.error("IO error: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Global exception handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("message", "An unexpected error occurred");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}