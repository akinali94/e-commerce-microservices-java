package com.example.cart_service.config;

import com.example.cart_service.repository.CartRepository;
import com.example.cart_service.repository.DynamoDBCartRepository;
import com.example.cart_service.repository.RedisCartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Factory for creating the appropriate CartRepository implementation
 * based on active Spring profiles.
 */
@Configuration
public class CartRepositoryFactory {

    private static final Logger logger = LoggerFactory.getLogger(CartRepositoryFactory.class);

    private final Environment environment;

    /**
     * Constructor with Environment dependency for profile checking.
     *
     * @param environment The Spring environment
     */
    @Autowired
    public CartRepositoryFactory(Environment environment) {
        this.environment = environment;
    }

    /**
     * Creates the appropriate CartRepository implementation.
     * Supports Redis and DynamoDB based on active profiles.
     *
     * @param redisCartRepository The Redis implementation (injected if available)
     * @param dynamoDBCartRepository The DynamoDB implementation (injected if available)
     * @return The appropriate CartRepository implementation
     */
    @Bean
    public CartRepository cartRepository(
            @Autowired(required = false) RedisCartRepository redisCartRepository,
            @Autowired(required = false) DynamoDBCartRepository dynamoDBCartRepository) {
        
        // Log active profiles
        logger.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        
        // Check if DynamoDB profile is active
        if (environment.matchesProfiles("dynamodb") && dynamoDBCartRepository != null) {
            logger.info("Using DynamoDB cart repository");
            return dynamoDBCartRepository;
        }
        
        // Check if Redis profile is active
        if (environment.matchesProfiles("redis") && redisCartRepository != null) {
            logger.info("Using Redis cart repository");
            return redisCartRepository;
        }
        
        // Default to Redis for now (if available)
        if (redisCartRepository != null) {
            logger.info("No specific profile active, defaulting to Redis cart repository");
            return redisCartRepository;
        }
        
        // If neither Redis nor DynamoDB is available, throw an exception
        throw new IllegalStateException(
                "No cart repository implementation available. Please activate either 'redis' or 'dynamodb' profile.");
    }
}