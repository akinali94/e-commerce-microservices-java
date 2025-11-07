package com.example.checkout_service.service;

import com.example.checkout_service.model.*;
import com.example.checkout_service.dto.*;
import com.example.checkout_service.exception.CurrencyConversionException;
import com.example.checkout_service.exception.InvalidRequestException;
import com.example.checkout_service.exception.ProductNotFoundException;
import com.example.checkout_service.exception.ShippingException;
import com.example.checkout_service.util.MoneyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceImpl.class);
    private static final String DEFAULT_CURRENCY = "USD";


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

        // Extract product IDs from cart items
        List<String> productIds = request.getItems().stream()
                    .map(CartItem::getProductId)
                    .collect(Collectors.toList());
            
            // 2. Get products using batch endpoint
            List<Product> products = productService.getMultipleProducts(productIds);
            
            // Create a map for easy lookup of products by ID
            Map<String, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            
            // Create order items with product information
            List<OrderItem> orderItems = new ArrayList<>();
            List<CartItem> cartItems = request.getItems();
            
            // 3. Calculate total cost using quantity from request and money from products
            Money total = new Money(0, 0, request.getUserCurrency());
            
            for (CartItem cartItem : cartItems) {
                Product product = productMap.get(cartItem.getProductId());
                if (product == null) {
                    logger.warn("Product not found for ID: {}", cartItem.getProductId());
                    throw new ProductNotFoundException(cartItem.getProductId());
                }
                
                // Ensure the product's price has a currency code
                Money productPrice = product.getPriceUsd();
                if (productPrice == null) {
                    logger.warn("Product price is null for ID: {}", cartItem.getProductId());
                    throw new InvalidRequestException("Product price is null for ID: " + cartItem.getProductId());
                }
                
                // Set default currency code if it's null
                if (productPrice.getCurrencyCode() == null) {
                    logger.warn("Product price currency code is null for ID: {}, defaulting to USD", cartItem.getProductId());
                    productPrice.setCurrencyCode(DEFAULT_CURRENCY);
                }
                
                // Convert product price to user currency if needed
                Money itemPrice;
                if (!productPrice.getCurrencyCode().equals(request.getUserCurrency())) {
                    try {
                        itemPrice = currencyService.convertCurrency(productPrice, request.getUserCurrency());
                    } catch (Exception e) {
                        throw new CurrencyConversionException("Failed to convert currency from " + 
                                                            productPrice.getCurrencyCode() + " to " + 
                                                            request.getUserCurrency(), e);
                    }
                } else {
                    itemPrice = productPrice;
                }
                
                // Create order item
                OrderItem orderItem = new OrderItem();
                orderItem.setItem(cartItem);
                orderItem.setCost(itemPrice);
                orderItems.add(orderItem);
                
                // Add to total
                Money itemTotal = MoneyUtil.multiply(itemPrice, cartItem.getQuantity());
                total = MoneyUtil.sum(total, itemTotal);
            }
            
            // 4. Get shipping quote
            Money shippingCostUSD;
            try {
                shippingCostUSD = shippingService.getShippingQuote(request.getAddress(), cartItems);
            } catch (Exception e) {
                throw new ShippingException("Failed to get shipping quote", e);
            }
            
            // Convert shipping cost to user currency if needed
            Money shippingCost;
            if (!shippingCostUSD.getCurrencyCode().equals(request.getUserCurrency())) {
                try {
                    shippingCost = currencyService.convertCurrency(shippingCostUSD, request.getUserCurrency());
                } catch (Exception e) {
                    throw new CurrencyConversionException("Failed to convert shipping cost currency", e);
                }
            } else {
                shippingCost = shippingCostUSD;
            }
            
            // 5. Add shipping cost to total
            total = MoneyUtil.sum(total, shippingCost);
            
            // 6. Process payment
            String transactionId = paymentService.chargeCard(total, request.getCreditCard());
            logger.info("Payment went through (transaction_id: {})", transactionId);
            
            // 7. Ship order
            String shippingTrackingId;
            try {
                shippingTrackingId = shippingService.shipOrder(request.getAddress(), cartItems);
            } catch (Exception e) {
                throw new ShippingException("Failed to ship order", e);
            }  

            // 8. Empty the user's cart if we were using it
            if (request.getUserId() != null && !request.getUserId().isEmpty()) {
                try {
                    cartService.emptyCart(request.getUserId());
                    logger.info("Cart emptied for user: {}", request.getUserId());
                } catch (Exception e) {
                    logger.warn("Failed to empty cart for user: {}", request.getUserId(), e);
                }
            }
            
            // 9. Create the order result
            OrderResult orderResult = new OrderResult(
                    orderId,
                    shippingTrackingId,
                    total,
                    shippingCost,
                    request.getAddress(),
                    orderItems);
            
            
            // 10. Send confirmation email
            try {
                emailService.sendOrderConfirmation(request.getEmail(), orderResult);
                logger.info("Order confirmation email sent to: {}", request.getEmail());
            } catch (Exception e) {
                logger.warn("Failed to send order confirmation to: {}", request.getEmail(), e);
            }
    
            return new PlaceOrderResponse("Your order is successfull", orderResult);
    }
}