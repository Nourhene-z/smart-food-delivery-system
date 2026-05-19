package com.designpatterns.fooddelivery.service;

import com.designpatterns.fooddelivery.dto.CreateOrderRequest;
import com.designpatterns.fooddelivery.dto.OrderDto;
import com.designpatterns.fooddelivery.entity.Order;
import com.designpatterns.fooddelivery.exception.ResourceNotFoundException;
import com.designpatterns.fooddelivery.mock.MockNotificationService;
import com.designpatterns.fooddelivery.repository.CustomerRepository;
import com.designpatterns.fooddelivery.repository.OrderRepository;
import com.designpatterns.fooddelivery.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Order operations.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final MockNotificationService notificationService;

    /**
     * Create a new order.
     *
     * @param createOrderRequest the order creation request
     * @return created order DTO
     * @throws ResourceNotFoundException if customer or restaurant not found
     */
    @Transactional
    public OrderDto createOrder(CreateOrderRequest createOrderRequest) {
        log.info("Creating new order for Customer: {}, Restaurant: {}",
                createOrderRequest.getCustomerId(), createOrderRequest.getRestaurantId());

        // Validate customer exists
        customerRepository.findById(createOrderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + createOrderRequest.getCustomerId()));

        // Validate restaurant exists
        restaurantRepository.findById(createOrderRequest.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with ID: " + createOrderRequest.getRestaurantId()));

        // Create order
        Order order = Order.builder()
                .customerId(createOrderRequest.getCustomerId())
                .restaurantId(createOrderRequest.getRestaurantId())
                .totalAmount(createOrderRequest.getTotalAmount())
                .deliveryAddress(createOrderRequest.getDeliveryAddress())
                .specialInstructions(createOrderRequest.getSpecialInstructions())
                .paymentMethod(createOrderRequest.getPaymentMethod())
                .status(Order.OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());

        // Send confirmation notification
        notificationService.sendOrderConfirmation(
                createOrderRequest.getCustomerId(), savedOrder.getId());

        return convertToDto(savedOrder);
    }

    /**
     * Get order by ID.
     *
     * @param orderId the order ID
     * @return order DTO
     * @throws ResourceNotFoundException if order not found
     */
    public OrderDto getOrderById(Long orderId) {
        log.debug("Fetching order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));
        return convertToDto(order);
    }

    /**
     * Get orders by customer.
     *
     * @param customerId the customer ID
     * @return list of orders
     */
    public List<OrderDto> getOrdersByCustomer(Long customerId) {
        log.debug("Fetching orders for Customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Update order status.
     *
     * @param orderId the order ID
     * @param newStatus the new status
     * @return updated order DTO
     */
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        log.info("Updating order {} status to: {}", orderId, newStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // Send status notification
        notificationService.sendDeliveryStatusUpdate(
                order.getCustomerId(), orderId, newStatus.toString());

        return convertToDto(updatedOrder);
    }

    /**
     * Cancel an order.
     *
     * @param orderId the order ID
     * @return updated order DTO
     */
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        return convertToDto(cancelledOrder);
    }

    /**
     * Convert Order entity to DTO.
     *
     * @param order the order entity
     * @return order DTO
     */
    private OrderDto convertToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .deliveryAddress(order.getDeliveryAddress())
                .specialInstructions(order.getSpecialInstructions())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
