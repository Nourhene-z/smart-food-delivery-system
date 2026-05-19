package com.designpatterns.fooddelivery.service;

import com.designpatterns.fooddelivery.dto.PaymentRequest;
import com.designpatterns.fooddelivery.dto.PaymentResponse;
import com.designpatterns.fooddelivery.entity.Order;
import com.designpatterns.fooddelivery.exception.PaymentException;
import com.designpatterns.fooddelivery.exception.ResourceNotFoundException;
import com.designpatterns.fooddelivery.mock.MockNotificationService;
import com.designpatterns.fooddelivery.mock.MockPaymentGateway;
import com.designpatterns.fooddelivery.repository.OrderRepository;
import com.designpatterns.fooddelivery.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentService.
 *
 * Tests the Strategy Pattern implementation for payment processing.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MockPaymentGateway mockPaymentGateway;

    @Mock
    private MockNotificationService notificationService;

    @InjectMocks
    private PaymentService paymentService;

    private Order testOrder;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id(1L)
                .customerId(1L)
                .restaurantId(1L)
                .totalAmount(50.0)
                .status(Order.OrderStatus.PENDING)
                .build();

        paymentRequest = PaymentRequest.builder()
                .orderId(1L)
                .amount(50.0)
                .paymentMethod("CREDIT_CARD")
                .build();
    }

    @Test
    void testProcessPayment_WithCreditCard_Success() {
        // Arrange
        CreditCardPayment strategy = new CreditCardPayment();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.getStrategy("CREDIT_CARD")).thenReturn(strategy);
        when(mockPaymentGateway.processPayment(1L, 50.0)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(1L, result.getOrderId());
        assertEquals(50.0, result.getAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testProcessPayment_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        PaymentRequest invalidRequest = PaymentRequest.builder()
                .orderId(999L)
                .amount(50.0)
                .paymentMethod("CREDIT_CARD")
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> paymentService.processPayment(invalidRequest));
    }

    @Test
    void testProcessPayment_InvalidPaymentMethod() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.getStrategy("INVALID_METHOD"))
                .thenThrow(new PaymentException("Unsupported payment method: INVALID_METHOD"));

        PaymentRequest invalidRequest = PaymentRequest.builder()
                .orderId(1L)
                .amount(50.0)
                .paymentMethod("INVALID_METHOD")
                .build();

        // Act & Assert
        assertThrows(PaymentException.class, () -> paymentService.processPayment(invalidRequest));
    }

    @Test
    void testRefundPayment_Success() {
        // Arrange
        testOrder.setPaymentMethod("CREDIT_CARD");
        CreditCardPayment strategy = new CreditCardPayment();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.getStrategy("CREDIT_CARD")).thenReturn(strategy);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        PaymentResponse result = paymentService.refundPayment(1L, 50.0);

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("REFUNDED", result.getPaymentStatus());
    }

    @Test
    void testRefundPayment_NoPaymentMethod() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(PaymentException.class, () -> paymentService.refundPayment(1L, 50.0));
    }
}
