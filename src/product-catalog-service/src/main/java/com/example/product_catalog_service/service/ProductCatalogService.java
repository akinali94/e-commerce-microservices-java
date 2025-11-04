package com.example.product_catalog_service.service;

import com.example.product_catalog_service.dto.ListProductsResponse;
import com.example.product_catalog_service.dto.SearchProductsResponse;
import com.example.product_catalog_service.exception.ProductNotFoundException;
import com.example.product_catalog_service.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    }
     
    /**
     * Get current catalog, optionally reloading based on flag
     */
    private List<Product> getCatalog() throws IOException {
        if (catalog.getProducts().isEmpty()) {
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
     * Get multiple products by their IDs
     */
    public ListProductsResponse getProductsById(List<String> ids) throws IOException {
        List<Product> products = getCatalog();
        
        List<Product> foundProducts = products.stream()
            .filter(p -> ids.contains(p.getId()))
            .collect(Collectors.toList());
        
        logger.info("Found {} products out of {} requested IDs", foundProducts.size(), ids.size());
        
        // Logged Id for missing products
        if (foundProducts.size() < ids.size()) {
            List<String> foundIds = foundProducts.stream().map(Product::getId).collect(Collectors.toList());
            List<String> missingIds = ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            logger.warn("The following product IDs were not found: {}", missingIds);
        }
        
        return new ListProductsResponse(foundProducts);
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