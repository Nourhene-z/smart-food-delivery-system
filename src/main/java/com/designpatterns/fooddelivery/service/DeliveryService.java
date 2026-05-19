package com.designpatterns.fooddelivery.service;

import com.designpatterns.fooddelivery.dto.DeliveryDto;
import com.designpatterns.fooddelivery.entity.Delivery;
import com.designpatterns.fooddelivery.entity.Order;
import com.designpatterns.fooddelivery.exception.ResourceNotFoundException;
import com.designpatterns.fooddelivery.mock.MockGpsService;
import com.designpatterns.fooddelivery.mock.MockNotificationService;
import com.designpatterns.fooddelivery.repository.DeliveryRepository;
import com.designpatterns.fooddelivery.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Service layer for Delivery operations.
 *
 * This service demonstrates the Circuit Breaker pattern by protecting
 * calls to the external GPS service with Resilience4j.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final MockGpsService mockGpsService;
    private final MockNotificationService notificationService;
    private static final Random random = new Random();

    /**
     * Assign delivery to an order.
     *
     * @param orderId the order ID
     * @return created delivery DTO
     * @throws ResourceNotFoundException if order not found
     */
    @Transactional
    public DeliveryDto assignDelivery(Long orderId) {
        log.info("Assigning delivery for Order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        // Check if delivery already exists
        if (deliveryRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalStateException("Delivery already assigned for Order: " + orderId);
        }

        // Create delivery
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .driverName("Driver-" + random.nextInt(1000))
                .driverPhone("+91" + random.nextInt(9000000000) + 1000000000)
                .vehicleNumber("MH-" + random.nextInt(999) + "AB" + random.nextInt(9999))
                .estimatedTimeMinutes(25 + random.nextInt(35))
                .deliveryStatus(Delivery.DeliveryStatus.ASSIGNED)
                .currentLatitude(12.9716)
                .currentLongitude(77.5946)
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery assigned with ID: {}", savedDelivery.getId());

        // Update order status
        order.setStatus(Order.OrderStatus.ASSIGNED_TO_DELIVERY);
        orderRepository.save(order);

        return convertToDto(savedDelivery);
    }

    /**
     * Get delivery information with Circuit Breaker protection for GPS service.
     *
     * This method demonstrates the Circuit Breaker Pattern by:
     * 1. Protecting calls to the external GPS service
     * 2. Using fallback when the service is unavailable
     * 3. Automatically recovering when the service becomes available again
     *
     * @param deliveryId the delivery ID
     * @return delivery DTO with current location
     * @throws ResourceNotFoundException if delivery not found
     */
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "gpsServiceCircuitBreaker", fallbackMethod = "getDeliveryFallback")
    @Retry(name = "deliveryTrackingRetry")
    public DeliveryDto getDeliveryWithLocation(Long deliveryId) {
        log.debug("Fetching delivery with location for ID: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery not found with ID: " + deliveryId));

        // Call GPS service to get current location
        try {
            DeliveryDto gpsData = mockGpsService.getDeliveryLocation(deliveryId);
            delivery.setCurrentLatitude(gpsData.getCurrentLatitude());
            delivery.setCurrentLongitude(gpsData.getCurrentLongitude());
            log.debug("GPS location updated for delivery: {}", deliveryId);
        } catch (Exception e) {
            log.warn("Error fetching GPS location for delivery {}: {}", deliveryId, e.getMessage());
            // Continue with cached location
        }

        return convertToDto(delivery);
    }

    /**
     * Fallback method for getDeliveryWithLocation when circuit is open.
     * Returns the last known location from the database.
     *
     * @param deliveryId the delivery ID
     * @param ex the exception
     * @return delivery DTO with cached location
     */
    public DeliveryDto getDeliveryFallback(Long deliveryId, Exception ex) {
        log.warn("Circuit breaker activated for delivery {}. Using cached location. Error: {}",
                deliveryId, ex.getMessage());

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery not found with ID: " + deliveryId));

        return convertToDto(delivery);
    }

    /**
     * Get delivery by order ID.
     *
     * @param orderId the order ID
     * @return delivery DTO
     * @throws ResourceNotFoundException if delivery not found
     */
    public DeliveryDto getDeliveryByOrderId(Long orderId) {
        log.debug("Fetching delivery for Order: {}", orderId);
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery not found for Order: " + orderId));
        return convertToDto(delivery);
    }

    /**
     * Update delivery status.
     *
     * @param deliveryId the delivery ID
     * @param newStatus the new status
     * @return updated delivery DTO
     */
    @Transactional
    public DeliveryDto updateDeliveryStatus(Long deliveryId, Delivery.DeliveryStatus newStatus) {
        log.info("Updating delivery {} status to: {}", deliveryId, newStatus);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery not found with ID: " + deliveryId));

        delivery.setDeliveryStatus(newStatus);
        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Update associated order status
        Order order = orderRepository.findById(delivery.getOrderId())
                .orElseThrow();

        if (newStatus == Delivery.DeliveryStatus.DELIVERED) {
            order.setStatus(Order.OrderStatus.DELIVERED);
        } else if (newStatus == Delivery.DeliveryStatus.IN_TRANSIT) {
            order.setStatus(Order.OrderStatus.IN_DELIVERY);
        }

        orderRepository.save(order);

        return convertToDto(updatedDelivery);
    }

    /**
     * Get active deliveries.
     *
     * @return list of active deliveries
     */
    public List<DeliveryDto> getActiveDeliveries() {
        log.debug("Fetching active deliveries");
        return deliveryRepository.findByDeliveryStatus(Delivery.DeliveryStatus.IN_TRANSIT)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Convert Delivery entity to DTO.
     *
     * @param delivery the delivery entity
     * @return delivery DTO
     */
    private DeliveryDto convertToDto(Delivery delivery) {
        return DeliveryDto.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .driverName(delivery.getDriverName())
                .driverPhone(delivery.getDriverPhone())
                .vehicleNumber(delivery.getVehicleNumber())
                .estimatedTimeMinutes(delivery.getEstimatedTimeMinutes())
                .actualTimeMinutes(delivery.getActualTimeMinutes())
                .deliveryStatus(delivery.getDeliveryStatus())
                .currentLatitude(delivery.getCurrentLatitude())
                .currentLongitude(delivery.getCurrentLongitude())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
