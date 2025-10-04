package com.example.product_catalog_service.service;

import com.example.productcatalog.dto.ListProductsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CatalogLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(CatalogLoader.class);
    
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    
    @Value("${rds.cluster.name:}")
    private String rdsClusterName;
    
    @Value("${catalog.file.path:classpath:data/products.json}")
    private String catalogFilePath;
    
    public CatalogLoader(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Load catalog from configured source (local file or RDS)
     */
    public ListProductsResponse loadCatalog() throws IOException {
        if (rdsClusterName != null && !rdsClusterName.isEmpty()) {
            return loadCatalogFromRDS();
        } else {
            return loadCatalogFromLocalFile();
        }
    }
    
    /**
     * Load catalog from local JSON file
     */
    private ListProductsResponse loadCatalogFromLocalFile() throws IOException {
        logger.info("Loading catalog from local file: {}", catalogFilePath);
        
        try {
            Resource resource = resourceLoader.getResource(catalogFilePath);
            
            if (!resource.exists()) {
                throw new IOException("Catalog file not found: " + catalogFilePath);
            }
            
            try (InputStream inputStream = resource.getInputStream()) {
                ListProductsResponse response = objectMapper.readValue(
                    inputStream, 
                    ListProductsResponse.class
                );
                
                logger.info("Successfully loaded {} products from file", 
                    response.getProducts().size());
                
                return response;
            }
            
        } catch (IOException e) {
            logger.error("Failed to load catalog from file: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load catalog from AWS RDS PostgreSQL
     * This will be implemented when database support is added
     */
    private ListProductsResponse loadCatalogFromRDS() throws IOException {
        logger.info("Loading catalog from AWS RDS...");
        
        // TODO: Implement RDS connection
        // - Get database credentials from AWS Secrets Manager
        // - Connect to RDS PostgreSQL using Spring Data JPA
        // - Query products table
        // - Map rows to Product objects
        
        throw new UnsupportedOperationException("RDS loading not yet implemented");
    }
}
