package com.example.currency_service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.currency_service.model.ConversionResponse;
import com.example.currency_service.model.SupportedCurrenciesResponse;
import com.example.currency_service.util.CurrencyDataLoader;
import com.example.currency_service.exception.UnsupportedCurrencyException;



public class CurrencyServiceImpl implements CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);
    private final CurrencyDataLoader dataLoader;

    // Cache
    private Map<String, Double> cachedData;
    private long lastLoadTime = 0;
    private static final long CACHE_DURATION = 60 * 60 * 1000; // 1 saat

    public CurrencyServiceImpl(CurrencyDataLoader dataLoader){
        this.dataLoader = dataLoader;
    }

    private Map<String, Double> getCurrencyData() {
        long now = System.currentTimeMillis();

        // Cache check
        if (cachedData != null && (now - lastLoadTime) < CACHE_DURATION) {
            logger.info("Using cached currency data");
            return cachedData;
        }

        // Load new data
        logger.info("Loading currency data from file...");
        cachedData = dataLoader.loadCurrencyData();
        lastLoadTime = now;
        logger.info("Currency data loaded successfully. {} currencies available", cachedData.size());

        return cachedData;
    }

    @Override
    public SupportedCurrenciesResponse getSupportedCurrencies(){
        logger.info("Getting supported currencies...");

        Map<String, Double> data = getCurrencyData();
        List<String> currencyCodes = new ArrayList<>(data.keySet());
        Collections.sort(currencyCodes); // Alphabetic Sort

        logger.info("Found {} supported currencies", currencyCodes.size());

        return new SupportedCurrenciesResponse(currencyCodes); 
    }

    @Override
    public ConversionResponse convertCurrency(String from, String to, double amount) {
        logger.info("Converting {} {} to {}...", amount, from, to);

        Map<String, Double> data = getCurrencyData();

        String fromUpper = from.toUpperCase();
        String toUpper = to.toUpperCase();

        // Validate currencies
        if (!data.containsKey(fromUpper)) {
            throw new UnsupportedCurrencyException("Unsupported currency: " + fromUpper);
        }

        if (!data.containsKey(toUpper)) {
            throw new UnsupportedCurrencyException("Unsupported currency: " + toUpper);
        }

        // Get rates
        double fromRate = data.get(fromUpper);
        double toRate = data.get(toUpper);

        // EUR based cross-rate calculation
        double eurAmount = amount / fromRate;
        double result = eurAmount * toRate;

        // round to 4 degree
        result = Math.round(result * 10000.0) / 10000.0;

        logger.info("Conversion successful: {} {} = {} {}", amount, fromUpper, result, toUpper);

        return new ConversionResponse(result, fromUpper, toUpper, amount);
    }

    public void clearCache() {
        logger.info("Clearing currency data cache...");
        cachedData = null;
        lastLoadTime = 0;
    }
}
