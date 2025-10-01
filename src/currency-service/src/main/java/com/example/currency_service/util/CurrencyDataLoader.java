package com.example.currency_service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencyDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyDataLoader.class);
    private static final String DATA_FILE = "data/currency_conversion.json";

    public Map<String, Double> loadCurrencyData() {
        try {
            ClassPathResource resource = new ClassPathResource(DATA_FILE);
            InputStream inputStream = resource.getInputStream();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> rawData = mapper.readValue(
                    inputStream,
                    new TypeReference<Map<String, String>>() {}
            );

            // String değerleri Double'a çevir
            Map<String, Double> currencyData = new HashMap<>();
            for (Map.Entry<String, String> entry : rawData.entrySet()) {
                try {
                    currencyData.put(entry.getKey(), Double.parseDouble(entry.getValue()));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid rate for currency {}: {}", entry.getKey(), entry.getValue());
                }
            }

            logger.info("Successfully loaded {} currencies from {}", currencyData.size(), DATA_FILE);
            return currencyData;

        } catch (IOException e) {
            logger.error("Failed to load currency data from {}", DATA_FILE, e);
            throw new RuntimeException("Failed to load currency data", e);
        }
    }
}