package com.designpatterns.fooddelivery.dto;

import com.designpatterns.fooddelivery.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Order responses.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long customerId;
    private Long restaurantId;
    private Double totalAmount;
    private Order.OrderStatus status;
    private String paymentMethod;
    private String deliveryAddress;
    private String specialInstructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
