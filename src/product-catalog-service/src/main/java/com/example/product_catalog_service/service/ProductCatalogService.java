package com.example.product_catalog_service.service;

import com.example.product_catalog_service.dto.ListProductsResponse;
import com.example.product_catalog_service.dto.SearchProductsResponse;
import com.example.product_catalog_service.exception.ProductNotFoundException;
import com.example.product_catalog_service.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.misc.Signal;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCatalogService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogService.class);
    
    private final CatalogLoader catalogLoader;
    
    private ListProductsResponse catalog;
    private volatile boolean reloadCatalog = false;
    
    public ProductCatalogService(CatalogLoader catalogLoader) {
        this.catalogLoader = catalogLoader;
        this.catalog = new ListProductsResponse(new ArrayList<>());
    }
    
    /**
     * Initialize catalog on application startup
     */
    @PostConstruct
    public void initialize() throws IOException {
        logger.info("Initializing product catalog...");
        
        // Load initial catalog
        this.catalog = catalogLoader.loadCatalog();
        logger.info("Catalog initialized with {} products", catalog.getProducts().size());
        
        // Setup signal handlers for dynamic reloading
        setupSignalHandlers();
    }
    
    /**
     * Setup signal handlers (USR1/USR2) for catalog reloading
     * Note: Signal handling in Java is platform-dependent and may not work on Windows
     */
    private void setupSignalHandlers() {
        try {
            // Enable catalog reloading on SIGUSR1
            Signal.handle(new Signal("USR1"), sig -> {
                logger.info("Received SIGUSR1: Enable catalog reloading");
                reloadCatalog = true;
            });
            
            // Disable catalog reloading on SIGUSR2
            Signal.handle(new Signal("USR2"), sig -> {
                logger.info("Received SIGUSR2: Disable catalog reloading");
                reloadCatalog = false;
            });
            
            logger.info("Signal handlers registered (SIGUSR1, SIGUSR2)");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Signal handling not supported on this platform: {}", e.getMessage());
        }
    }
    
    /**
     * Get current catalog, optionally reloading based on flag
     */
    private List<Product> getCatalog() throws IOException {
        if (reloadCatalog || catalog.getProducts().isEmpty()) {
            catalog = catalogLoader.loadCatalog();
        }
        return catalog.getProducts();
    }
    
    /**
     * List all products
     */
    public ListProductsResponse listProducts() throws IOException {
        List<Product> products = getCatalog();
        return new ListProductsResponse(products);
    }
    
    /**
     * Get a single product by ID
     */
    public Product getProduct(String id) throws IOException {
        List<Product> products = getCatalog();
        
        return products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    /**
     * Search products by query (case-insensitive search in name and description)
     */
    public SearchProductsResponse searchProducts(String query) throws IOException {
        List<Product> products = getCatalog();
        String lowerQuery = query.toLowerCase();
        
        List<Product> results = products.stream()
            .filter(product -> 
                product.getName().toLowerCase().contains(lowerQuery) ||
                product.getDescription().toLowerCase().contains(lowerQuery)
            )
            .collect(Collectors.toList());
        
        return new SearchProductsResponse(results);
    }
}