package com.designpatterns.fooddelivery.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Mock Payment Gateway Service simulating external payment processing API.
 *
 * This service simulates an external payment gateway with:
 * - Variable success rates
 * - Timeout simulation
 * - Network failure simulation
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@Slf4j
public class MockPaymentGateway {

    private static final Random random = new Random();

    /**
     * Process payment through the external gateway.
     *
     * @param orderId the order ID
     * @param amount the payment amount
     * @return true if payment is successful
     * @throws RuntimeException if payment processing fails
     */
    public boolean processPayment(Long orderId, Double amount) {
        log.debug("MockPaymentGateway: Processing payment for Order: {}, Amount: {}", orderId, amount);

        // Simulate timeout (2% chance)
        if (random.nextDouble() < 0.02) {
            log.error("MockPaymentGateway: Payment timeout for Order: {}", orderId);
            throw new RuntimeException("Payment gateway timeout");
        }

        // Simulate network error (3% chance)
        if (random.nextDouble() < 0.03) {
            log.error("MockPaymentGateway: Network error for Order: {}", orderId);
            throw new RuntimeException("Payment gateway network error");
        }

        // Simulate payment decline (2% chance)
        if (random.nextDouble() < 0.02) {
            log.warn("MockPaymentGateway: Payment declined for Order: {}", orderId);
            return false;
        }

        log.info("MockPaymentGateway: Payment successful for Order: {}, Amount: {}", orderId, amount);
        return true;
    }
}
