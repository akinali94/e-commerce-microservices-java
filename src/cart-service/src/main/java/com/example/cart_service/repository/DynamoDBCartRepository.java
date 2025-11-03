package com.example.cart_service.repository;

import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DynamoDB implementation of the CartRepository interface.
 * This implementation is only active when the "dynamodb" profile is active.
 */
@Repository
@Profile("dynamodb")
public class DynamoDBCartRepository /*implements CartRepository*/ {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBCartRepository.class);
    
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    
    /**
     * Constructor with DynamoDbClient and table name from configuration.
     *
     * @param dynamoDbClient The DynamoDB client
     * @param tableName The DynamoDB table name from configuration
     */
    public DynamoDBCartRepository(
            DynamoDbClient dynamoDbClient,
            @Value("${aws.dynamodb.table.name}") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        
        // Ensure the table exists
        createTableIfNotExists();
    }

    /**
     * Creates the DynamoDB table if it doesn't exist.
     */
    private void createTableIfNotExists() {
        try {
            // Check if table already exists
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            logger.info("DynamoDB table '{}' already exists", tableName);
        } catch (ResourceNotFoundException e) {
            // Table doesn't exist, create it
            logger.info("Creating DynamoDB table '{}'", tableName);
            
            // Create the table
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("userId")
                                    .keyType(KeyType.HASH)
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName("productId")
                                    .keyType(KeyType.RANGE)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("userId")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("productId")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
            
            // Create table and wait until it's active
            dynamoDbClient.createTable(request);
            
            logger.info("Waiting for DynamoDB table '{}' to become active", tableName);
            DynamoDbWaiter waiter = dynamoDbClient.waiter();
            WaiterResponse<DescribeTableResponse> response = waiter.waitUntilTableExists(
                    DescribeTableRequest.builder().tableName(tableName).build());
            
            response.matched().response().ifPresent(r -> 
                    logger.info("DynamoDB table '{}' is now active", tableName));
        }
    }

    //@Override
    public CompletableFuture<Void> addItem(String userId, String productId, int quantity) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Adding item to cart for user: {}, product: {}, quantity: {}", 
                        userId, productId, quantity);
                
                // Check if item already exists
                GetItemResponse existingItem = dynamoDbClient.getItem(GetItemRequest.builder()
                        .tableName(tableName)
                        .key(Map.of(
                                "userId", AttributeValue.builder().s(userId).build(),
                                "productId", AttributeValue.builder().s(productId).build()
                        ))
                        .build());
                
                int totalQuantity = quantity;
                
                // If item exists, add to its quantity
                if (existingItem.hasItem()) {
                    int currentQuantity = Integer.parseInt(existingItem.item().get("quantity").n());
                    totalQuantity += currentQuantity;
                }
                
                // Update or insert the item
                dynamoDbClient.putItem(PutItemRequest.builder()
                        .tableName(tableName)
                        .item(Map.of(
                                "userId", AttributeValue.builder().s(userId).build(),
                                "productId", AttributeValue.builder().s(productId).build(),
                                "quantity", AttributeValue.builder().n(String.valueOf(totalQuantity)).build()
                        ))
                        .build());
            } catch (Exception e) {
                logger.error("Failed to add item to cart in DynamoDB", e);
                throw new RuntimeException("Failed to add item to cart: " + e.getMessage(), e);
            }
        });
    }

    //@Override
    public CompletableFuture<Cart> getCart(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Getting cart for user: {}", userId);
                
                // Query all items for the user
                QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                        .tableName(tableName)
                        .keyConditionExpression("userId = :userId")
                        .expressionAttributeValues(Map.of(
                                ":userId", AttributeValue.builder().s(userId).build()
                        ))
                        .build());
                
                // Convert query results to Cart object
                List<CartItem> items = response.items().stream()
                        .map(item -> {
                            String productId = item.get("productId").s();
                            int quantity = Integer.parseInt(item.get("quantity").n());
                            return new CartItem(productId, quantity);
                        })
                        .collect(Collectors.toList());
                
                // Create the cart
                Cart cart = new Cart();
                cart.setUserId(userId);
                cart.setItems(items);
                
                return cart;
            } catch (Exception e) {
                logger.error("Failed to get cart from DynamoDB", e);
                throw new RuntimeException("Failed to get cart: " + e.getMessage(), e);
            }
        });
    }

    //@Override
    public CompletableFuture<Void> emptyCart(String userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Emptying cart for user: {}", userId);
                
                // First, get all items in the cart
                QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                        .tableName(tableName)
                        .keyConditionExpression("userId = :userId")
                        .expressionAttributeValues(Map.of(
                                ":userId", AttributeValue.builder().s(userId).build()
                        ))
                        .build());
                
                // If cart is already empty, return
                if (response.items().isEmpty()) {
                    return;
                }
                
                // Batch delete all items
                List<WriteRequest> deleteRequests = response.items().stream()
                        .map(item -> WriteRequest.builder()
                                .deleteRequest(DeleteRequest.builder()
                                        .key(Map.of(
                                                "userId", AttributeValue.builder().s(userId).build(),
                                                "productId", AttributeValue.builder().s(item.get("productId").s()).build()
                                        ))
                                        .build())
                                .build())
                        .collect(Collectors.toList());
                
                // DynamoDB BatchWriteItem can only process up to 25 items at a time
                List<List<WriteRequest>> batches = new ArrayList<>();
                for (int i = 0; i < deleteRequests.size(); i += 25) {
                    batches.add(deleteRequests.subList(i, 
                            Math.min(i + 25, deleteRequests.size())));
                }
                
                // Process each batch
                for (List<WriteRequest> batch : batches) {
                    dynamoDbClient.batchWriteItem(BatchWriteItemRequest.builder()
                            .requestItems(Map.of(tableName, batch))
                            .build());
                }
            } catch (Exception e) {
                logger.error("Failed to empty cart in DynamoDB", e);
                throw new RuntimeException("Failed to empty cart: " + e.getMessage(), e);
            }
        });
    }

    //@Override
    public boolean isHealthy() {
        try {
            // Check connection by describing the table
            dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build());
            return true;
        } catch (Exception e) {
            logger.error("DynamoDB health check failed", e);
            return false;
        }
    }
}