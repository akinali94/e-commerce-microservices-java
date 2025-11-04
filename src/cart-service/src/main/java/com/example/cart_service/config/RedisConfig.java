package com.example.cart_service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis configuration that uses Jedis client with custom serialization.
 */
@Configuration
@Profile("redis")
public class RedisConfig {
    
    @Value("${spring.data.redis.host}")
    private String redisHost;
    
    @Value("${spring.data.redis.port}")
    private int redisPort;
    
    @Value("${spring.data.redis.password:}")
    private String redisPassword;
    
    @Value("${spring.data.redis.timeout:2000}")
    private int timeout;
    
    /**
     * Configures a Jedis pool for Redis connections.
     * 
     * @return A configured JedisPoolConfig
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);
        config.setMaxIdle(8);
        config.setMinIdle(0);
        return config;
    }
    
    /**
     * Creates a RedisConnectionFactory using Jedis.
     * 
     * @return A JedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }
        
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.setPoolConfig(jedisPoolConfig);
        return factory;
    }
    
    /**
     * Creates a custom ObjectMapper for Redis serialization.
     * 
     * @return A configured ObjectMapper
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Handle unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Other useful configurations
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        return mapper;
    }
    
    /**
     * Creates a Redis template with custom serializers.
     * 
     * @param connectionFactory The Redis connection factory
     * @param objectMapper The custom ObjectMapper for JSON serialization
     * @return A configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper redisObjectMapper) {
        // Create our custom serializer that produces clean JSON without type information
        CustomCartRedisSerializer customSerializer = new CustomCartRedisSerializer(redisObjectMapper);
        
        // Create and configure the RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use string serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        
        // Use our custom serializer for values and hash values
        template.setValueSerializer(customSerializer);
        template.setHashValueSerializer(customSerializer);
        
        // Use string serializer for hash keys
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Set default serializer
        template.setEnableDefaultSerializer(true);
        template.setDefaultSerializer(customSerializer);
        
        template.afterPropertiesSet();
        
        return template;
    }
}