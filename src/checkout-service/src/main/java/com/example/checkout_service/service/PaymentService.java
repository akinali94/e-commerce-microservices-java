package com.example.checkout_service.service;

import com.example.checkout_service.model.CreditCardInfo;
import com.example.checkout_service.model.Money;

public interface PaymentService {
    String chargeCard(Money amount, CreditCardInfo creditCard);
}