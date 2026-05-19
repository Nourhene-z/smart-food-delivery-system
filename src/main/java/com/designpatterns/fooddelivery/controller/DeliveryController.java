package com.designpatterns.fooddelivery.controller;

import com.designpatterns.fooddelivery.dto.ApiResponse;
import com.designpatterns.fooddelivery.dto.DeliveryDto;
import com.designpatterns.fooddelivery.entity.Delivery;
import com.designpatterns.fooddelivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Delivery Management endpoints.
 *
 * Demonstrates the Circuit Breaker Pattern when fetching
 * delivery location from external GPS service.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * Assign delivery to an order.
     *
     * @param orderId the order ID
     * @return created delivery response
     */
    @PostMapping("/{orderId}/assign")
    public ResponseEntity<ApiResponse<DeliveryDto>> assignDelivery(@PathVariable Long orderId) {
        log.info("POST /api/delivery/{}/assign - Assigning delivery", orderId);
        DeliveryDto deliveryDto = deliveryService.assignDelivery(orderId);
        ApiResponse<DeliveryDto> response = ApiResponse.success(
                deliveryDto, "Delivery assigned successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get delivery information with location.
     *
     * This endpoint demonstrates the Circuit Breaker Pattern by protecting
     * calls to the external GPS service. When the GPS service is unavailable,
     * the circuit breaker automatically falls back to cached location data.
     *
     * @param deliveryId the delivery ID
     * @return delivery with current location
     */
    @GetMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryDto>> getDeliveryWithLocation(
            @PathVariable Long deliveryId) {
        log.info("GET /api/delivery/{} - Fetching delivery with location", deliveryId);
        DeliveryDto deliveryDto = deliveryService.getDeliveryWithLocation(deliveryId);
        ApiResponse<DeliveryDto> response = ApiResponse.success(
                deliveryDto, "Delivery retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get delivery by order ID.
     *
     * @param orderId the order ID
     * @return delivery information
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<DeliveryDto>> getDeliveryByOrderId(
            @PathVariable Long orderId) {
        log.info("GET /api/delivery/order/{} - Fetching delivery for order", orderId);
        DeliveryDto deliveryDto = deliveryService.getDeliveryByOrderId(orderId);
        ApiResponse<DeliveryDto> response = ApiResponse.success(
                deliveryDto, "Delivery retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update delivery status.
     *
     * @param deliveryId the delivery ID
     * @param status the new status
     * @return updated delivery response
     */
    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<ApiResponse<DeliveryDto>> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestParam Delivery.DeliveryStatus status) {
        log.info("PUT /api/delivery/{}/status - Updating delivery status to {}", deliveryId, status);
        DeliveryDto deliveryDto = deliveryService.updateDeliveryStatus(deliveryId, status);
        ApiResponse<DeliveryDto> response = ApiResponse.success(
                deliveryDto, "Delivery status updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get active deliveries.
     *
     * @return list of active deliveries
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DeliveryDto>>> getActiveDeliveries() {
        log.info("GET /api/delivery/active - Fetching active deliveries");
        List<DeliveryDto> deliveries = deliveryService.getActiveDeliveries();
        ApiResponse<List<DeliveryDto>> response = ApiResponse.success(
                deliveries, "Active deliveries retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
