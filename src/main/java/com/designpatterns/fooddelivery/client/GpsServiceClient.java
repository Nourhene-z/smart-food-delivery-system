package com.designpatterns.fooddelivery.client;

import com.designpatterns.fooddelivery.dto.DeliveryDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for GPS/Location Service.
 *
 * This client communicates with an external GPS service using the Circuit Breaker
 * and Retry patterns to handle failures gracefully.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@FeignClient(name = "gpsService", url = "http://localhost:8081/api/gps")
public interface GpsServiceClient {

    /**
     * Get delivery location information with Circuit Breaker protection.
     *
     * @param deliveryId the delivery ID
     * @return delivery information with current location
     */
    @GetMapping("/{deliveryId}")
    @CircuitBreaker(name = "gpsServiceCircuitBreaker", fallbackMethod = "getLocationFallback")
    @Retry(name = "deliveryTrackingRetry")
    DeliveryDto getDeliveryLocation(@PathVariable Long deliveryId);

    /**
     * Fallback method for getDeliveryLocation when the circuit is open.
     *
     * @param deliveryId the delivery ID
     * @param ex the exception that caused the circuit to open
     * @return estimated delivery information
     */
    default DeliveryDto getLocationFallback(Long deliveryId, Exception ex) {
        return DeliveryDto.builder()
                .id(deliveryId)
                .currentLatitude(12.9716)
                .currentLongitude(77.5946)
                .deliveryStatus(null)
                .build();
    }
}
