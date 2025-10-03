package com.example.payment_service.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.payment_service.exception.ExpiredCreditCardException;
import com.example.payment_service.exception.InvalidCreditCardException;
import com.example.payment_service.exception.UnacceptedCreditCardException;
import com.example.payment_service.model.Amount;
import com.example.payment_service.model.CardDetails;
import com.example.payment_service.model.ChargeResponse;
import com.example.payment_service.model.CreditCard;
import com.example.payment_service.util.CreditCardValidity;

public class PaymentServiceImpl implements PaymentService{

    private final CreditCardValidity validator;
    private static final Logger logger = LoggerFactory.getLogger(CreditCardValidity.class);

    public PaymentServiceImpl(CreditCardValidity cardValidator){
        this.validator = cardValidator;
    }

    @Override
    public ChargeResponse charge(Amount amount, CreditCard card){   

        CardDetails details = validator.cardValidation(card);

        if(!details.getValid()){
            throw new InvalidCreditCardException("Credit Card is not Valid");
        }

        if((!details.getCardType().equals("Mastercard")) ||
        (!details.getCardType().equals("Visa")) ||
        (!details.getCardType().equals("American Express"))){
            throw new UnacceptedCreditCardException("Only Visa, Mastercard and American Express can be used");
        }

        if(!details.getIsNotExpired()){
            throw new ExpiredCreditCardException("Credit Card is expired");
        }

        logger.info(String.format(
            "Transaction processed: %s ending %s Amount: %s%d.%d",
            details.getCardType(),
            validator.mask(card.getCardNo()),
            amount.getCurrencyCode(),
            amount.getUnits(),
            amount.getNanos()
        ));        

        return new ChargeResponse(UUID.randomUUID().toString());
    }
}
