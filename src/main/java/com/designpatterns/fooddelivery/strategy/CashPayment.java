package com.designpatterns.fooddelivery.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Cash Payment Strategy implementation.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Component
@Slf4j
public class CashPayment implements PaymentStrategy {

    @Override
    public boolean processPayment(Long orderId, Double amount) {
        // Cash payment is always successful (marked as pending verification)
        String transactionId = "CASH-" + System.currentTimeMillis();
        log.info("Cash Payment recorded for Order: {}, Amount: {}, Transaction ID: {}",
                orderId, amount, transactionId);
        return true;
    }

    @Override
    public boolean refundPayment(Long orderId, Double amount) {
        // Cash refund will be processed by delivery personnel
        String transactionId = "CASH-REFUND-" + System.currentTimeMillis();
        log.info("Cash Refund recorded for Order: {}, Amount: {}, Transaction ID: {}",
                orderId, amount, transactionId);
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "CASH";
    }
}
