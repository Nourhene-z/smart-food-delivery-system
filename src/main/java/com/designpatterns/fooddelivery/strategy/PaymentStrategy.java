package com.designpatterns.fooddelivery.strategy;

/**
 * Strategy Pattern Interface for different payment methods.
 *
 * This interface defines a contract for various payment strategies
 * allowing the application to select a payment method dynamically
 * based on user preference.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
public interface PaymentStrategy {

    /**
     * Process payment using the specific strategy implementation.
     *
     * @param orderId the order ID for which payment is being processed
     * @param amount the amount to be paid
     * @return true if payment is successful, false otherwise
     */
    boolean processPayment(Long orderId, Double amount);

    /**
     * Refund a payment that was previously processed.
     *
     * @param orderId the order ID for which refund is being requested
     * @param amount the amount to be refunded
     * @return true if refund is successful, false otherwise
     */
    boolean refundPayment(Long orderId, Double amount);

    /**
     * Get the name of the payment strategy.
     *
     * @return the name of the payment method
     */
    String getPaymentMethodName();
}
