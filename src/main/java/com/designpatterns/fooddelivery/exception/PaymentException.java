package com.designpatterns.fooddelivery.exception;

/**
 * Custom exception for invalid payment operations.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
