package com.designpatterns.fooddelivery.strategy;

import com.designpatterns.fooddelivery.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for selecting the appropriate PaymentStrategy based on payment method.
 *
 * This factory implements the Strategy Pattern to dynamically select
 * the appropriate payment processing implementation.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentStrategyFactory {

    private final CreditCardPayment creditCardPayment;
    private final PayPalPayment payPalPayment;
    private final CashPayment cashPayment;

    /**
     * Get the appropriate payment strategy based on payment method.
     *
     * @param paymentMethod the payment method type
     * @return the corresponding PaymentStrategy implementation
     * @throws PaymentException if the payment method is not supported
     */
    public PaymentStrategy getStrategy(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new PaymentException("Payment method cannot be null or empty");
        }

        log.debug("Creating payment strategy for method: {}", paymentMethod);

        return switch (paymentMethod.toUpperCase()) {
            case "CREDIT_CARD", "CREDITCARD", "CC" -> creditCardPayment;
            case "PAYPAL", "PP" -> payPalPayment;
            case "CASH" -> cashPayment;
            default -> throw new PaymentException("Unsupported payment method: " + paymentMethod);
        };
    }
}
