package com.example.checkout_service.exception;

public class InvalidMoneyException extends Exception {
    public InvalidMoneyException(String message) {
        super(message);
    }

    public InvalidMoneyException(String message, Throwable cause) {
        super(message, cause);
    }
}