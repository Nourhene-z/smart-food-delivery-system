package com.designpatterns.fooddelivery.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Mock Notification Service for sending customer notifications.
 *
 * Simulates sending emails, SMS, and push notifications.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@Slf4j
public class MockNotificationService {

    private static final Random random = new Random();

    /**
     * Send order confirmation notification.
     *
     * @param customerId the customer ID
     * @param orderId the order ID
     * @return true if notification is sent successfully
     */
    public boolean sendOrderConfirmation(Long customerId, Long orderId) {
        log.info("MockNotificationService: Sending order confirmation - Customer: {}, Order: {}",
                customerId, orderId);

        // Simulate occasional failures
        if (random.nextDouble() < 0.02) {
            log.error("Failed to send order confirmation for Order: {}", orderId);
            return false;
        }

        return true;
    }

    /**
     * Send delivery status update notification.
     *
     * @param customerId the customer ID
     * @param orderId the order ID
     * @param status the delivery status
     * @return true if notification is sent successfully
     */
    public boolean sendDeliveryStatusUpdate(Long customerId, Long orderId, String status) {
        log.info("MockNotificationService: Sending delivery status update - Customer: {}, Order: {}, Status: {}",
                customerId, orderId, status);

        // Simulate occasional failures
        if (random.nextDouble() < 0.02) {
            log.error("Failed to send status update for Order: {}", orderId);
            return false;
        }

        return true;
    }

    /**
     * Send payment confirmation notification.
     *
     * @param customerId the customer ID
     * @param orderId the order ID
     * @param amount the payment amount
     * @return true if notification is sent successfully
     */
    public boolean sendPaymentConfirmation(Long customerId, Long orderId, Double amount) {
        log.info("MockNotificationService: Sending payment confirmation - Customer: {}, Order: {}, Amount: {}",
                customerId, orderId, amount);

        // Simulate occasional failures
        if (random.nextDouble() < 0.02) {
            log.error("Failed to send payment confirmation for Order: {}", orderId);
            return false;
        }

        return true;
    }
}
