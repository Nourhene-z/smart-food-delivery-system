package com.designpatterns.fooddelivery.service;

import com.designpatterns.fooddelivery.dto.PaymentRequest;
import com.designpatterns.fooddelivery.dto.PaymentResponse;
import com.designpatterns.fooddelivery.entity.Order;
import com.designpatterns.fooddelivery.exception.PaymentException;
import com.designpatterns.fooddelivery.exception.ResourceNotFoundException;
import com.designpatterns.fooddelivery.mock.MockNotificationService;
import com.designpatterns.fooddelivery.mock.MockPaymentGateway;
import com.designpatterns.fooddelivery.repository.OrderRepository;
import com.designpatterns.fooddelivery.strategy.PaymentStrategy;
import com.designpatterns.fooddelivery.strategy.PaymentStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Payment operations.
 *
 * This service demonstrates the Strategy Pattern by dynamically selecting
 * the appropriate payment strategy based on the payment method.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentStrategyFactory paymentStrategyFactory;
    private final OrderRepository orderRepository;
    private final MockPaymentGateway mockPaymentGateway;
    private final MockNotificationService notificationService;

    /**
     * Process payment using the appropriate strategy.
     *
     * This method demonstrates the Strategy Pattern by:
     * 1. Getting the appropriate PaymentStrategy based on payment method
     * 2. Delegating payment processing to the selected strategy
     *
     * @param request the payment request
     * @return payment response
     * @throws PaymentException if payment fails
     * @throws ResourceNotFoundException if order not found
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for Order: {}, Amount: {}, Method: {}",
                request.getOrderId(), request.getAmount(), request.getPaymentMethod());

        // Validate order exists
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + request.getOrderId()));

        // Get the appropriate payment strategy
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(request.getPaymentMethod());

        // Process payment through the external gateway
        boolean paymentSuccess = false;
        try {
            paymentSuccess = mockPaymentGateway.processPayment(
                    request.getOrderId(), request.getAmount());
        } catch (Exception e) {
            log.error("External payment gateway error: {}", e.getMessage());
            // Continue with strategy-based processing
        }

        // Process payment using the selected strategy
        boolean strategyProcessed = paymentStrategy.processPayment(
                request.getOrderId(), request.getAmount());

        if (!paymentSuccess && !strategyProcessed) {
            throw new PaymentException("Payment processing failed. Please try again.");
        }

        // Update order status
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setPaymentMethod(request.getPaymentMethod());
        orderRepository.save(order);

        // Send notification
        notificationService.sendPaymentConfirmation(
                order.getCustomerId(), order.getId(), request.getAmount());

        String transactionId = paymentStrategy.getPaymentMethodName() + "-" + System.currentTimeMillis();

        log.info("Payment processed successfully. Transaction ID: {}", transactionId);

        return PaymentResponse.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .success(true)
                .transactionId(transactionId)
                .message("Payment processed successfully")
                .paymentStatus("COMPLETED")
                .build();
    }

    /**
     * Refund a payment.
     *
     * @param orderId the order ID
     * @param amount the refund amount
     * @return refund response
     */
    @Transactional
    public PaymentResponse refundPayment(Long orderId, Double amount) {
        log.info("Processing refund for Order: {}, Amount: {}", orderId, amount);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new PaymentException("Order has no payment method recorded");
        }

        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(paymentMethod);
        boolean refundSuccess = paymentStrategy.refundPayment(orderId, amount);

        if (!refundSuccess) {
            throw new PaymentException("Refund processing failed");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        return PaymentResponse.builder()
                .orderId(orderId)
                .amount(amount)
                .success(true)
                .transactionId("REFUND-" + System.currentTimeMillis())
                .message("Refund processed successfully")
                .paymentStatus("REFUNDED")
                .build();
    }
}
