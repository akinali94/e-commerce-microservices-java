package com.example.cart_service.config;

import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Redis serializer that converts Cart objects to simplified JSON without type information.
 */
public class CustomCartRedisSerializer implements RedisSerializer<Object> {

    private static final Logger logger = LoggerFactory.getLogger(CustomCartRedisSerializer.class);
    private final Charset charset = StandardCharsets.UTF_8;
    private final ObjectMapper objectMapper;

    public CustomCartRedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }

        try {
            // Special handling for Cart objects
            if (object instanceof Cart) {
                Cart cart = (Cart) object;
                Map<String, Object> simplified = new LinkedHashMap<>();
                simplified.put("userId", cart.getUserId());
                
                // Convert CartItem list to simplified format
                List<Map<String, Object>> items = new ArrayList<>();
                for (CartItem item : cart.getItems()) {
                    Map<String, Object> itemMap = new LinkedHashMap<>();
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("quantity", item.getQuantity());
                    items.add(itemMap);
                }
                simplified.put("items", items);
                
                logger.debug("Serializing cart to simplified format: {}", simplified);
                return objectMapper.writeValueAsString(simplified).getBytes(charset);
            }
            
            // Default serialization for other objects
            return objectMapper.writeValueAsString(object).getBytes(charset);
        } catch (Exception ex) {
            logger.error("Error serializing object", ex);
            throw new SerializationException("Could not serialize object", ex);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            String json = new String(bytes, charset);
            logger.debug("Deserializing JSON: {}", json);
            
            // Parse as Map first to determine the object type
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            
            // Check if this looks like a Cart
            if (map.containsKey("userId") && map.containsKey("items")) {
                Cart cart = new Cart();
                cart.setUserId((String) map.get("userId"));
                
                // Parse items
                List<CartItem> items = new ArrayList<>();
                List<Object> rawItems = (List<Object>) map.get("items");
                
                for (Object rawItem : rawItems) {
                    Map<String, Object> itemMap = (Map<String, Object>) rawItem;
                    String productId = (String) itemMap.get("productId");
                    
                    // Handle different number types (Long from JSON vs Integer)
                    int quantity;
                    Object quantityObj = itemMap.get("quantity");
                    if (quantityObj instanceof Integer) {
                        quantity = (Integer) quantityObj;
                    } else if (quantityObj instanceof Long) {
                        quantity = ((Long) quantityObj).intValue();
                    } else {
                        quantity = Integer.parseInt(quantityObj.toString());
                    }
                    
                    items.add(new CartItem(productId, quantity));
                }
                
                cart.setItems(items);
                logger.debug("Deserialized cart: {}", cart);
                return cart;
            }
            
            // Default deserialization (as LinkedHashMap)
            return map;
        } catch (Exception ex) {
            logger.error("Error deserializing bytes", ex);
            throw new SerializationException("Could not deserialize bytes", ex);
        }
    }
}