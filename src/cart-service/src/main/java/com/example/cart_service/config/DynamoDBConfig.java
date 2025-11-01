package com.example.cart_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * Configuration for AWS DynamoDB.
 * This configuration is only active when the "dynamodb" profile is active.
 */
@Configuration
@Profile("dynamodb")
public class DynamoDBConfig {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.dynamodb.endpoint:#{null}}")
    private String dynamodbEndpoint;

    @Value("${aws.access-key:#{null}}")
    private String accessKey;

    @Value("${aws.secret-key:#{null}}")
    private String secretKey;

    /**
     * Creates a DynamoDB client with appropriate configuration.
     *
     * @return The DynamoDB client
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        // Builder for DynamoDB client
        DynamoDbClientBuilder builder = DynamoDbClient.builder();
        
        // Set region
        builder.region(Region.of(awsRegion));
        
        // Set credentials provider
        builder.credentialsProvider(getCredentialsProvider());
        
        // Set endpoint if provided (useful for local development with DynamoDB Local)
        if (dynamodbEndpoint != null && !dynamodbEndpoint.isEmpty()) {
            builder.endpointOverride(URI.create(dynamodbEndpoint));
        }
        
        return builder.build();
    }

    /**
     * Gets the AWS credentials provider based on configuration.
     *
     * @return The AWS credentials provider
     */
    private AwsCredentialsProvider getCredentialsProvider() {
        // If both access key and secret key are provided, use static credentials provider
        if (accessKey != null && !accessKey.isEmpty() && secretKey != null && !secretKey.isEmpty()) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        }
        
        // Otherwise, use default credentials provider chain
        // This will look for credentials in the following order:
        // 1. Java system properties
        // 2. Environment variables
        // 3. Web Identity Token (for EKS)
        // 4. AWS profile file
        // 5. Amazon ECS container credentials
        // 6. EC2 instance profile credentials
        return DefaultCredentialsProvider.create();
    }
}