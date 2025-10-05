package com.example.shipping_service.util;

import com.example.shipping_service.model.Money;
import com.example.shipping_service.model.Quote;
import org.springframework.stereotype.Component;

@Component
public class QuoteUtil {
    
    /**
     * Creates a quote from a count of items
     * Note: Currently returns fixed $8.99 regardless of count
     */
    public Quote createQuoteFromCount(int count) {
        return createQuoteFromFloat(8.99);
    }
    
    /**
     * Converts a float price to a Quote object
     */
    public Quote createQuoteFromFloat(double value) {
        int units = (int) Math.floor(value);
        double fraction = value - units;
        
        Quote quote = new Quote();
        quote.setDollars(units);
        quote.setCents((int) Math.floor(fraction * 100));
        
        return quote;
    }
    
    /**
     * Converts a Quote to Money format (for API responses)
     */
    public Money quoteToMoney(Quote quote) {
        Money money = new Money();
        money.setCurrencyCode("USD");
        money.setUnits(quote.getDollars().longValue());
        money.setNanos(quote.getCents() * 10000000); // Convert cents to nanoseconds
        
        return money;
    }
    
    /**
     * Formats a Quote as a string (e.g., "$8.99")
     */
    public String formatQuote(Quote quote) {
        return String.format("$%d.%02d", quote.getDollars(), quote.getCents());
    }
}