package com.example.payment_service.service;

import java.util.UUID;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.payment_service.exception.ExpiredCreditCardException;
import com.example.payment_service.exception.InvalidCreditCardException;
import com.example.payment_service.exception.UnacceptedCreditCardException;
import com.example.payment_service.model.Amount;
import com.example.payment_service.model.CardDetails;
import com.example.payment_service.dto.ChargeResponse;
import com.example.payment_service.model.CreditCard;
import com.example.payment_service.util.CreditCardValidity;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final CreditCardValidity validator;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final List<String> ACCEPTED_CARD_TYPES = Arrays.asList("Mastercard", "Visa", "American Express");

    public PaymentServiceImpl(CreditCardValidity cardValidator) {
        this.validator = cardValidator;
    }

    @Override
    public ChargeResponse charge(Amount amount, CreditCard card) {   
        logger.debug("Validating credit card");
        CardDetails details = validator.cardValidation(card);

        if (!details.getValid()) {
            logger.warn("Invalid credit card detected");
            throw new InvalidCreditCardException("Credit Card is not valid");
        }

        if (!ACCEPTED_CARD_TYPES.contains(details.getCardType())) {
            logger.warn("Unaccepted card type: {}", details.getCardType());
            throw new UnacceptedCreditCardException(
                "Card type not accepted. Only Visa, Mastercard and American Express can be used");
        }

        if (!details.getIsNotExpired()) {
            logger.warn("Expired credit card detected");
            throw new ExpiredCreditCardException("Credit Card is expired");
        }

        String transactionId = UUID.randomUUID().toString();
        
        logger.info("Transaction processed: id={}, cardType={}, maskedCard={}, amount={}{}",
            transactionId,
            details.getCardType(),
            validator.mask(card.getCardNo()),
            amount.getCurrencyCode(),
            formatAmount(amount.getUnits(), amount.getNanos())
        );        

        return new ChargeResponse(transactionId);
    }
    
    private String formatAmount(int units, int nanos) {
        return String.format("%d.%02d", units, nanos / 10_000_000);
    }
}