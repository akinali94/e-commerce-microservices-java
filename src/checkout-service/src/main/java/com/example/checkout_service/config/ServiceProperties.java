package com.example.checkout_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for external services.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {
    
    private ServiceConfig cart;
    private ServiceConfig productCatalog;
    private ServiceConfig currency;
    private ServiceConfig shipping;
    private ServiceConfig payment;
    private ServiceConfig email;
    
    @Data
    public static class ServiceConfig {
        private String url;
        private String basePath;
        
        /**
         * Gets the full base URL by combining url and basePath
         */
        public String getFullUrl() {
            if (basePath == null || basePath.isEmpty()) {
                return url;
            }
            return url + basePath;
        }
    }
}