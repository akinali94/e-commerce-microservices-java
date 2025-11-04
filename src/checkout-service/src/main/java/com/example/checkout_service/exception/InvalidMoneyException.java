package com.example.checkout_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidMoneyException extends ServiceException {
    public InvalidMoneyException(String message) {
        super(message, "INVALID_MONEY_ERROR", HttpStatus.BAD_REQUEST.value());
    }

    public InvalidMoneyException(String message, Throwable cause) {
        super(message, "INVALID_MONEY_ERROR", HttpStatus.BAD_REQUEST.value(), cause);
    }
}