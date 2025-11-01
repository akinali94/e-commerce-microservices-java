package com.example.checkout_service.service;

import com.example.checkout_service.model.*;
import com.example.checkout_service.dto.*;
import com.example.checkout_service.service.*;
import com.example.checkout_service.util.MoneyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final CartService cartService;
    private final ProductCatalogService productService;
    private final CurrencyService currencyService;
    private final ShippingService shippingService;
    private final PaymentService paymentService;
    private final EmailService emailService;

    public CheckoutServiceImpl(CartService cartService,
                             ProductCatalogService productService,
                             CurrencyService currencyService,
                             ShippingService shippingService,
                             PaymentService paymentService,
                             EmailService emailService) {
        this.cartService = cartService;
        this.productService = productService;
        this.currencyService = currencyService;
        this.shippingService = shippingService;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        logger.info("PlaceOrder called by userId: {}, with currency: {}", 
                   request.getUserId(), request.getUserCurrency());

        // Generate a random order ID
        String orderId = UUID.randomUUID().toString();

        // Prepare items and get shipping quote
        OrderPrep orderPrep = prepareOrderItemsAndShippingQuoteFromCart(
                request.getUserId(), 
                request.getUserCurrency(), 
                request.getAddress());

        // Calculate the total cost
        Money total = new Money(0, 0, request.getUserCurrency());
        try {
            // Add shipping cost to total
            total = MoneyUtil.sum(total, orderPrep.shippingCostLocalized);
            
            // Add the costs of all items
            for (OrderItem item : orderPrep.orderItems) {
                Money itemCost = item.getCost();
                int quantity = item.getItem().getQuantity();
                
                Money itemTotal = MoneyUtil.multiplySlow(itemCost, quantity);
                total = MoneyUtil.sum(total, itemTotal);
            }
        } catch (Exception e) {
            logger.error("Error calculating order total", e);
            throw new RuntimeException("Failed to calculate order total", e);
        }

        // Process payment
        String transactionId = paymentService.chargeCard(total, request.getCreditCard());
        logger.info("Payment went through (transaction_id: {})", transactionId);

        // Ship order
        String shippingTrackingId = shippingService.shipOrder(request.getAddress(), orderPrep.cartItems);

        // Empty the user's cart
        cartService.emptyCart(request.getUserId());

        // Create the order result
        OrderResult orderResult = new OrderResult(
                orderId,
                shippingTrackingId,
                orderPrep.shippingCostLocalized,
                request.getAddress(),
                orderPrep.orderItems);

        // Send confirmation email
        try {
            emailService.sendOrderConfirmation(request.getEmail(), orderResult);
            logger.info("Order confirmation email sent to: {}", request.getEmail());
        } catch (Exception e) {
            logger.warn("Failed to send order confirmation to: {}", request.getEmail(), e);
        }

        return new PlaceOrderResponse(orderResult);
    }

    private OrderPrep prepareOrderItemsAndShippingQuoteFromCart(
            String userId, String userCurrency, Address address) {
        OrderPrep orderPrep = new OrderPrep();

        // Get the user's cart
        List<CartItem> cartItems = cartService.getCart(userId);
        orderPrep.cartItems = cartItems;

        // Prepare the order items with prices
        orderPrep.orderItems = prepOrderItems(cartItems, userCurrency);

        // Get shipping quote
        Money shippingCostUSD = shippingService.getShippingQuote(address, cartItems);
        orderPrep.shippingCostLocalized = currencyService.convertCurrency(shippingCostUSD, userCurrency);

        return orderPrep;
    }

    private List<OrderItem> prepOrderItems(List<CartItem> items, String userCurrency) {
        List<OrderItem> orderItems = new ArrayList<>(items.size());

        for (CartItem item : items) {
            ProductCatalogService.Product product = productService.getProduct(item.getProductId());
            Money price = currencyService.convertCurrency(product.getPriceUsd(), userCurrency);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCost(price);
            
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private static class OrderPrep {
        private List<OrderItem> orderItems;
        private List<CartItem> cartItems;
        private Money shippingCostLocalized;
    }
}