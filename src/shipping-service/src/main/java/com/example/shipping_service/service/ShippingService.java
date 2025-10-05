package com.example.shipping_service.service;

import com.example.shipping_service.dto.ShipOrderRequest;
import com.example.shipping_service.dto.ShipOrderResponse;
import com.example.shipping_service.dto.ShippingQuoteRequest;
import com.example.shipping_service.dto.ShippingQuoteResponse;
import com.example.shipping_service.model.CartItem;
import com.example.shipping_service.model.Money;
import com.example.shipping_service.model.Quote;
import com.example.shipping_service.util.LoggerUtil;
import com.example.shipping_service.util.QuoteUtil;
import com.example.shipping_service.util.TrackerUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingService {
    
    private final QuoteUtil quoteUtil;
    private final TrackerUtil trackerUtil;
    private final LoggerUtil logger;
    
    public ShippingService(QuoteUtil quoteUtil, TrackerUtil trackerUtil, LoggerUtil logger) {
        this.quoteUtil = quoteUtil;
        this.trackerUtil = trackerUtil;
        this.logger = logger;
    }
    
    /**
     * Gets a shipping quote based on address and items
     */
    public ShippingQuoteResponse getQuote(ShippingQuoteRequest request) {
        Map<String, Object> logMeta = new HashMap<>();
        logMeta.put("city", request.getAddress().getCity());
        logMeta.put("itemCount", request.getItems().size());
        logger.info("[GetQuote] received request", logMeta);
        
        try {
            // Calculate total item count
            int totalItems = request.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
            
            // Generate quote based on count
            // Note: Current implementation returns fixed $8.99 regardless of count
            Quote quote = quoteUtil.createQuoteFromCount(totalItems);
            
            // Convert to Money format
            Money costUsd = quoteUtil.quoteToMoney(quote);
            
            Map<String, Object> completeMeta = new HashMap<>();
            completeMeta.put("totalItems", totalItems);
            completeMeta.put("cost", String.format("%d.%d", costUsd.getUnits(), costUsd.getNanos()));
            logger.info("[GetQuote] completed request", completeMeta);
            
            ShippingQuoteResponse response = new ShippingQuoteResponse();
            response.setCostUsd(costUsd);
            
            return response;
        } catch (Exception e) {
            Map<String, Object> errorMeta = new HashMap<>();
            errorMeta.put("error", e.getMessage());
            logger.error("[GetQuote] error processing request", errorMeta);
            throw e;
        }
    }
    
    /**
     * Ships an order and returns a tracking ID
     */
    public ShipOrderResponse shipOrder(ShipOrderRequest request) {
        Map<String, Object> logMeta = new HashMap<>();
        logMeta.put("city", request.getAddress().getCity());
        logMeta.put("itemCount", request.getItems().size());
        logger.info("[ShipOrder] received request", logMeta);
        
        try {
            // Create base address string for tracking ID
            String baseAddress = trackerUtil.formatAddressForTracking(request.getAddress());
            
            // Generate tracking ID
            String trackingId = trackerUtil.createTrackingId(baseAddress);
            
            Map<String, Object> completeMeta = new HashMap<>();
            completeMeta.put("trackingId", trackingId);
            completeMeta.put("addressLength", baseAddress.length());
            logger.info("[ShipOrder] completed request", completeMeta);
            
            ShipOrderResponse response = new ShipOrderResponse();
            response.setTrackingId(trackingId);
            
            return response;
        } catch (Exception e) {
            Map<String, Object> errorMeta = new HashMap<>();
            errorMeta.put("error", e.getMessage());
            logger.error("[ShipOrder] error processing request", errorMeta);
            throw e;
        }
    }
}