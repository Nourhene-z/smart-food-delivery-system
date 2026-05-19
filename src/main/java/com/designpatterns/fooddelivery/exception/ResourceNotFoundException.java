package com.designpatterns.fooddelivery.exception;

/**
 * Custom exception for resource not found scenarios.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
