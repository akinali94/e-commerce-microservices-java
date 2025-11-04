package com.example.shipping_service.service;

import com.example.shipping_service.dto.ShipOrderRequest;
import com.example.shipping_service.dto.ShipOrderResponse;
import com.example.shipping_service.dto.ShippingQuoteRequest;
import com.example.shipping_service.dto.ShippingQuoteResponse;
import com.example.shipping_service.exception.BadRequestException;
import com.example.shipping_service.exception.BusinessException;
import com.example.shipping_service.model.Address;
import com.example.shipping_service.model.CartItem;
import com.example.shipping_service.model.Money;
import com.example.shipping_service.model.Quote;
import com.example.shipping_service.util.QuoteUtil;
import com.example.shipping_service.util.TrackerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ShippingService.class);
    
    private final QuoteUtil quoteUtil;
    private final TrackerUtil trackerUtil;
    
    public ShippingService(QuoteUtil quoteUtil, TrackerUtil trackerUtil) {
        this.quoteUtil = quoteUtil;
        this.trackerUtil = trackerUtil;
    }
    
    /**
     * Gets a shipping quote based on address and items
     */
    public ShippingQuoteResponse getQuote(ShippingQuoteRequest request) {
        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }
        
        if (request.getAddress() == null) {
            request.setAddress(new Address());
        }
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Items list cannot be empty");
        }
        
        logger.info("Processing quote request for city: {}, with {} items", 
                   request.getAddress().getCity(), request.getItems().size());
        
        try {
            // Calculate total item count
            int totalItems = request.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
            
            // Validate item count
            if (totalItems <= 0) {
                throw new BusinessException("Total quantity must be greater than zero", "INVALID_QUANTITY");
            }
            
            // Generate quote based on count
            // Note: Current implementation returns fixed $8.99 regardless of count
            Quote quote = quoteUtil.createQuoteFromCount(totalItems);
            
            // Convert to Money format
            Money costUsd = quoteUtil.quoteToMoney(quote);
            
            logger.info("Quote calculated successfully: totalItems={}, cost=${}.{}",
                      totalItems, costUsd.getUnits(), costUsd.getNanos());
            
            ShippingQuoteResponse response = new ShippingQuoteResponse();
            response.setCostUsd(costUsd);
            
            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to calculate shipping quote", e);
            throw new BusinessException("Failed to calculate shipping quote: " + e.getMessage(), 
                                      "QUOTE_CALCULATION_FAILED");
        }
    }
    
    /**
     * Ships an order and returns a tracking ID
     */
    public ShipOrderResponse shipOrder(ShipOrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }
        
        if (request.getAddress() == null) {
            throw new BadRequestException("Shipping address is required");
        }
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Items list cannot be empty");
        }
        
        logger.info("Processing ship order request for city: {}, with {} items", 
                  request.getAddress().getCity(), request.getItems().size());
        
        try {
            // Validate zip code if required for shipping
            if (request.getAddress().getZipCode() == null) {
                throw new BusinessException("Zip code is required for shipping", "MISSING_ZIP_CODE");
            }
            
            // Create base address string for tracking ID
            String baseAddress = trackerUtil.formatAddressForTracking(request.getAddress());
            if (baseAddress == null || baseAddress.trim().isEmpty()) {
                throw new BusinessException("Unable to generate tracking ID with provided address", 
                                          "INVALID_ADDRESS_FORMAT");
            }
            
            // Generate tracking ID
            String trackingId = trackerUtil.createTrackingId(baseAddress);
            
            logger.info("Order shipped successfully: trackingId={}", trackingId);
            
            ShipOrderResponse response = new ShipOrderResponse();
            response.setTrackingId(trackingId);
            
            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to ship order", e);
            throw new BusinessException("Failed to ship order: " + e.getMessage(), 
                                      "SHIPPING_FAILED");
        }
    }
}