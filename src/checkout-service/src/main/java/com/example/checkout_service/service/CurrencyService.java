package com.example.checkout_service.service;

import com.example.checkout_service.model.Money;

public interface CurrencyService {
    Money convertCurrency(Money from, String toCurrency);
}