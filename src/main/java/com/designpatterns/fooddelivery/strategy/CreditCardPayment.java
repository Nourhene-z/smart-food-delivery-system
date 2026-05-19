package com.designpatterns.fooddelivery.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Credit Card Payment Strategy implementation.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class CreditCardPayment implements PaymentStrategy {

    private static final Random random = new Random();

    @Override
    public boolean processPayment(Long orderId, Double amount) {
        // Simulate credit card processing with occasional failures
        boolean success = random.nextDouble() > 0.05; // 95% success rate

        if (success) {
            String transactionId = "CC-" + System.currentTimeMillis();
            log.info("Credit Card Payment successful for Order: {}, Amount: {}, Transaction ID: {}",
                    orderId, amount, transactionId);
        } else {
            log.warn("Credit Card Payment failed for Order: {}, Amount: {}", orderId, amount);
        }

        return success;
    }

    @Override
    public boolean refundPayment(Long orderId, Double amount) {
        String transactionId = "CC-REFUND-" + System.currentTimeMillis();
        log.info("Credit Card Refund processed for Order: {}, Amount: {}, Transaction ID: {}",
                orderId, amount, transactionId);
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "CREDIT_CARD";
    }
}
