package com.designpatterns.fooddelivery.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * PayPal Payment Strategy implementation.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class PayPalPayment implements PaymentStrategy {

    private static final Random random = new Random();

    @Override
    public boolean processPayment(Long orderId, Double amount) {
        // Simulate PayPal processing with slightly higher reliability
        boolean success = random.nextDouble() > 0.03; // 97% success rate

        if (success) {
            String transactionId = "PP-" + System.currentTimeMillis();
            log.info("PayPal Payment successful for Order: {}, Amount: {}, Transaction ID: {}",
                    orderId, amount, transactionId);
        } else {
            log.warn("PayPal Payment failed for Order: {}, Amount: {}", orderId, amount);
        }

        return success;
    }

    @Override
    public boolean refundPayment(Long orderId, Double amount) {
        String transactionId = "PP-REFUND-" + System.currentTimeMillis();
        log.info("PayPal Refund processed for Order: {}, Amount: {}, Transaction ID: {}",
                orderId, amount, transactionId);
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "PAYPAL";
    }
}
