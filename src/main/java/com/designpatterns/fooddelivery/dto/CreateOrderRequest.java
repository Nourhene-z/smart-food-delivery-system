package com.designpatterns.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new order request.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    @NotNull(message = "Restaurant ID cannot be null")
    private Long restaurantId;

    @NotNull(message = "Total amount cannot be null")
    private Double totalAmount;

    @NotBlank(message = "Delivery address cannot be blank")
    private String deliveryAddress;

    private String specialInstructions;

    @NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod;
}
