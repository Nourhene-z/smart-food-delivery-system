package com.designpatterns.fooddelivery.controller;

import com.designpatterns.fooddelivery.dto.ApiResponse;
import com.designpatterns.fooddelivery.dto.CreateOrderRequest;
import com.designpatterns.fooddelivery.dto.OrderDto;
import com.designpatterns.fooddelivery.entity.Order;
import com.designpatterns.fooddelivery.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Order Management endpoints.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order.
     *
     * @param request the order creation request
     * @return created order response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - Creating new order");
        OrderDto orderDto = orderService.createOrder(request);
        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get order by ID.
     *
     * @param orderId the order ID
     * @return order response
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(@PathVariable Long orderId) {
        log.info("GET /api/orders/{} - Fetching order", orderId);
        OrderDto orderDto = orderService.getOrderById(orderId);
        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get orders by customer.
     *
     * @param customerId the customer ID
     * @return list of orders
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getCustomerOrders(@PathVariable Long customerId) {
        log.info("GET /api/orders/customer/{} - Fetching customer orders", customerId);
        List<OrderDto> orders = orderService.getOrdersByCustomer(customerId);
        ApiResponse<List<OrderDto>> response = ApiResponse.success(orders, "Orders retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update order status.
     *
     * @param orderId the order ID
     * @param status the new status
     * @return updated order response
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status) {
        log.info("PUT /api/orders/{}/status - Updating order status to {}", orderId, status);
        OrderDto orderDto = orderService.updateOrderStatus(orderId, status);
        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order status updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Cancel an order.
     *
     * @param orderId the order ID
     * @return cancelled order response
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(@PathVariable Long orderId) {
        log.info("DELETE /api/orders/{} - Cancelling order", orderId);
        OrderDto orderDto = orderService.cancelOrder(orderId);
        ApiResponse<OrderDto> response = ApiResponse.success(orderDto, "Order cancelled successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
