package com.designpatterns.fooddelivery.dto;

import com.designpatterns.fooddelivery.entity.Delivery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Delivery responses.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryDto {
    private Long id;
    private Long orderId;
    private String driverName;
    private String driverPhone;
    private String vehicleNumber;
    private Integer estimatedTimeMinutes;
    private Integer actualTimeMinutes;
    private Delivery.DeliveryStatus deliveryStatus;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
