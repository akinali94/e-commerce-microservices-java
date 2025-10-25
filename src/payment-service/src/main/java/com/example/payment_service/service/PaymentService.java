package com.example.payment_service.service;

import com.example.payment_service.dto.ChargeResponse;
import com.example.payment_service.model.Amount;
import com.example.payment_service.model.CreditCard;

public interface PaymentService {
    ChargeResponse charge(Amount amount, CreditCard card);
}
