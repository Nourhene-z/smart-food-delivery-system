package com.designpatterns.fooddelivery.service;

import com.designpatterns.fooddelivery.dto.CreateOrderRequest;
import com.designpatterns.fooddelivery.dto.OrderDto;
import com.designpatterns.fooddelivery.entity.Customer;
import com.designpatterns.fooddelivery.entity.Order;
import com.designpatterns.fooddelivery.entity.Restaurant;
import com.designpatterns.fooddelivery.exception.ResourceNotFoundException;
import com.designpatterns.fooddelivery.mock.MockNotificationService;
import com.designpatterns.fooddelivery.repository.CustomerRepository;
import com.designpatterns.fooddelivery.repository.OrderRepository;
import com.designpatterns.fooddelivery.repository.RestaurantRepository;
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
 * Unit tests for OrderService.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MockNotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Restaurant testRestaurant;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .address("123 Main St")
                .build();

        testRestaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Place")
                .category("Italian")
                .address("456 Restaurant Ave")
                .build();

        testOrder = Order.builder()
                .id(1L)
                .customerId(1L)
                .restaurantId(1L)
                .totalAmount(50.0)
                .status(Order.OrderStatus.PENDING)
                .deliveryAddress("123 Main St")
                .build();
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(1L)
                .restaurantId(1L)
                .totalAmount(50.0)
                .deliveryAddress("123 Main St")
                .paymentMethod("CREDIT_CARD")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderDto result = orderService.createOrder(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(notificationService, times(1)).sendOrderConfirmation(1L, 1L);
    }

    @Test
    void testCreateOrder_CustomerNotFound() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(999L)
                .restaurantId(1L)
                .totalAmount(50.0)
                .deliveryAddress("123 Main St")
                .build();

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderDto result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderDto result = orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderDto result = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
