package com.example.checkout_service.service;

import com.example.checkout_service.dto.ExternalServiceDtos;
import com.example.checkout_service.dto.PlaceOrderRequest;
import com.example.checkout_service.dto.PlaceOrderResponse;
import com.example.checkout_service.exception.EmptyCartException;
import com.example.checkout_service.exception.OrderPlacementException;
import com.example.checkout_service.model.*;
import com.example.checkout_service.util.LoggingUtil;
import com.example.checkout_service.util.MoneyUtil;
import com.example.checkout_service.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Main checkout service that orchestrates the order placement process.
 * This service coordinates multiple downstream services to complete an order.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutService {
    
    private final CartServiceClient cartServiceClient;
    private final ProductCatalogServiceClient productCatalogServiceClient;
    private final CurrencyServiceClient currencyServiceClient;
    private final ShippingServiceClient shippingServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final EmailServiceClient emailServiceClient;
    
    private static final String USD_CURRENCY = "USD";
    
    /**
     * Places an order for a user.
     * This is the main orchestration method that coordinates all services.
     * 
     * Workflow:
     * 1. Generate order ID
     * 2. Get user's cart and prepare order items
     * 3. Calculate shipping cost
     * 4. Calculate total amount
     * 5. Charge credit card
     * 6. Ship the order
     * 7. Empty user's cart
     * 8. Send confirmation email
     * 9. Return order result
     * 
     * @param request the place order request
     * @return the place order response with order details
     */
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Set logging context
        LoggingUtil.setUserId(request.getUserId());
        
        String orderId = UuidUtil.generateUuid();
        LoggingUtil.setOrderId(orderId);
        
        log.info("Starting order placement for user: {} with currency: {}", 
                request.getUserId(), request.getUserCurrency());
        
        try {
            // Step 1: Prepare order items and get shipping quote
            OrderPreparation prep = prepareOrderItemsAndShippingQuote(
                    request.getUserId(),
                    request.getUserCurrency(),
                    request.getAddress()
            );
            
            // Step 2: Calculate total amount
            Money total = calculateTotal(prep, request.getUserCurrency());
            
            log.info("Order total: {}", MoneyUtil.format(total));
            
            // Step 3: Charge the credit card
            String transactionId = chargeCard(total, request.getCreditCard());
            
            // Step 4: Ship the order
            String trackingId = shipOrder(request.getAddress(), prep.getCartItems());
            
            // Step 5: Empty user's cart (non-critical operation)
            emptyUserCart(request.getUserId());
            
            // Step 6: Build order result
            OrderResult orderResult = OrderResult.builder()
                    .orderId(orderId)
                    .shippingTrackingId(trackingId)
                    .shippingCost(prep.getShippingCostLocalized())
                    .shippingAddress(request.getAddress())
                    .items(prep.getOrderItems())
                    .build();
            
            // Step 7: Send confirmation email (non-critical operation)
            sendOrderConfirmation(request.getEmail(), orderResult);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Order placed successfully. Order ID: {}, Transaction ID: {}, Tracking ID: {}, Duration: {}ms",
                    orderId, transactionId, trackingId, duration);
            
            return PlaceOrderResponse.builder()
                    .order(orderResult)
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to place order for user {}: {}", request.getUserId(), e.getMessage(), e);
            throw new OrderPlacementException("Failed to place order: " + e.getMessage(), e);
        } finally {
            LoggingUtil.clearMDC();
        }
    }
    
    /**
     * Prepares order items and gets shipping quote.
     * This method retrieves cart items, gets product details, converts prices,
     * and calculates shipping cost.
     */
    private OrderPreparation prepareOrderItemsAndShippingQuote(
            String userId, String userCurrency, Address address) {
        
        log.debug("Preparing order items and shipping quote for user: {}", userId);
        
        // Get user's cart
        List<CartItem> cartItems = getUserCart(userId);
        
        if (cartItems == null || cartItems.isEmpty()) {
            throw new EmptyCartException(userId);
        }
        
        log.info("User cart contains {} items", cartItems.size());
        
        // Prepare order items with prices in user's currency
        List<OrderItem> orderItems = prepareOrderItems(cartItems, userCurrency);
        
        // Get shipping quote in USD
        Money shippingUsd = getShippingQuote(address, cartItems);
        
        // Convert shipping cost to user's currency
        Money shippingCostLocalized = convertCurrency(shippingUsd, userCurrency);
        
        return OrderPreparation.builder()
                .orderItems(orderItems)
                .cartItems(cartItems)
                .shippingCostLocalized(shippingCostLocalized)
                .build();
    }
    
    /**
     * Gets the user's cart items.
     */
    private List<CartItem> getUserCart(String userId) {
        log.debug("Getting cart for user: {}", userId);
        ExternalServiceDtos.GetCartResponse cart = cartServiceClient.getCart(userId);
        
        if (cart == null || cart.getItems() == null) {
            throw new EmptyCartException(userId);
        }
        
        return cart.getItems();
    }
    
    /**
     * Prepares order items by fetching product details and converting prices.
     */
    private List<OrderItem> prepareOrderItems(List<CartItem> cartItems, String userCurrency) {
        log.debug("Preparing {} order items", cartItems.size());
        
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            // Get product details
            ExternalServiceDtos.Product product = productCatalogServiceClient.getProduct(
                    cartItem.getProductId());
            
            if (product == null || product.getPriceUsd() == null) {
                throw new OrderPlacementException(
                        "Failed to get product details for product: " + cartItem.getProductId());
            }
            
            // Convert price to user's currency
            Money priceInUserCurrency = convertCurrency(product.getPriceUsd(), userCurrency);
            
            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .item(cartItem)
                    .cost(priceInUserCurrency)
                    .build();
            
            orderItems.add(orderItem);
            
            log.debug("Added order item: product={}, quantity={}, cost={}", 
                    cartItem.getProductId(), cartItem.getQuantity(), 
                    MoneyUtil.format(priceInUserCurrency));
        }
        
        return orderItems;
    }
    
    /**
     * Gets a shipping quote for the given address and items.
     */
    private Money getShippingQuote(Address address, List<CartItem> items) {
        log.debug("Getting shipping quote for {} items", items.size());
        return shippingServiceClient.getQuote(address, items);
    }
    
    /**
     * Converts money from one currency to another.
     */
    private Money convertCurrency(Money from, String toCurrency) {
        return currencyServiceClient.convertCurrency(from, toCurrency);
    }
    
    /**
     * Calculates the total order amount.
     * Total = sum of all items (price * quantity) + shipping cost
     */
    private Money calculateTotal(OrderPreparation prep, String currency) {
        log.debug("Calculating order total");
        
        // Start with shipping cost
        Money total = Money.builder()
                .units(0L)
                .nanos(0)
                .currencyCode(currency)
                .build();
        
        // Add shipping cost
        total = MoneyUtil.sum(total, prep.getShippingCostLocalized());
        
        // Add each item cost (price * quantity)
        for (OrderItem orderItem : prep.getOrderItems()) {
            Money itemCost = orderItem.getCost();
            int quantity = orderItem.getItem().getQuantity();
            
            // Multiply item cost by quantity
            Money itemTotal = MoneyUtil.multiply(itemCost, quantity);
            
            // Add to total
            total = MoneyUtil.sum(total, itemTotal);
        }
        
        return total;
    }
    
    /**
     * Charges the credit card for the specified amount.
     */
    private String chargeCard(Money amount, CreditCardInfo creditCard) {
        log.info("Charging card for amount: {}", MoneyUtil.format(amount));
        String transactionId = paymentServiceClient.chargeCard(amount, creditCard);
        log.info("Payment successful. Transaction ID: {}", transactionId);
        return transactionId;
    }
    
    /**
     * Ships the order to the specified address.
     */
    private String shipOrder(Address address, List<CartItem> items) {
        log.info("Shipping order to: {}, {}, {}", 
                address.getCity(), address.getState(), address.getCountry());
        String trackingId = shippingServiceClient.shipOrder(address, items);
        log.info("Order shipped. Tracking ID: {}", trackingId);
        return trackingId;
    }
    
    /**
     * Empties the user's cart after successful order.
     * This is a non-critical operation.
     */
    private void emptyUserCart(String userId) {
        try {
            log.debug("Emptying cart for user: {}", userId);
            cartServiceClient.emptyCart(userId);
        } catch (Exception e) {
            log.warn("Failed to empty cart for user {}: {}", userId, e.getMessage());
            // Don't fail the order if cart emptying fails
        }
    }
    
    /**
     * Sends order confirmation email.
     * This is a non-critical operation.
     */
    private void sendOrderConfirmation(String email, OrderResult order) {
        try {
            log.debug("Sending order confirmation to: {}", email);
            emailServiceClient.sendOrderConfirmation(email, order);
        } catch (Exception e) {
            log.warn("Failed to send order confirmation to {}: {}", email, e.getMessage());
            // Don't fail the order if email sending fails
        }
    }
    
    /**
     * Helper class to hold order preparation data.
     */
    @lombok.Data
    @lombok.Builder
    private static class OrderPreparation {
        private List<OrderItem> orderItems;
        private List<CartItem> cartItems;
        private Money shippingCostLocalized;
    }
}