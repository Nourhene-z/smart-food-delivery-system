package com.designpatterns.fooddelivery.mock;

import com.designpatterns.fooddelivery.dto.DeliveryDto;
import com.designpatterns.fooddelivery.entity.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Mock GPS Service simulating external GPS/Location API.
 *
 * This service simulates an external GPS service with:
 * - Random timeout simulation
 * - Forced failure mode
 * - Slow response simulation
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@Slf4j
public class MockGpsService {

    private static final Random random = new Random();
    private volatile boolean simulateFailure = false;
    private volatile boolean simulateSlowResponse = false;

    /**
     * Get current delivery location from GPS service.
     *
     * @param deliveryId the delivery ID
     * @return delivery information with current location
     * @throws RuntimeException if simulating failure
     */
    public DeliveryDto getDeliveryLocation(Long deliveryId) {
        log.debug("MockGpsService: Fetching location for delivery: {}", deliveryId);

        // Simulate slow responses
        if (simulateSlowResponse) {
            try {
                Thread.sleep(8000); // 8 seconds delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Simulate random failures
        if (simulateFailure || random.nextDouble() < 0.1) { // 10% failure rate
            log.error("MockGpsService: Simulated failure for delivery: {}", deliveryId);
            throw new RuntimeException("GPS service temporarily unavailable");
        }

        // Simulate random timeout
        if (random.nextDouble() < 0.05) { // 5% timeout rate
            throw new RuntimeException("GPS service timeout");
        }

        // Generate realistic delivery location
        Double latitude = 12.9716 + (random.nextDouble() * 0.1 - 0.05);
        Double longitude = 77.5946 + (random.nextDouble() * 0.1 - 0.05);

        return DeliveryDto.builder()
                .id(deliveryId)
                .deliveryStatus(Delivery.DeliveryStatus.IN_TRANSIT)
                .currentLatitude(latitude)
                .currentLongitude(longitude)
                .estimatedTimeMinutes(random.nextInt(30) + 5)
                .build();
    }

    /**
     * Enable failure simulation mode.
     */
    public void enableFailureSimulation() {
        this.simulateFailure = true;
        log.info("GPS Service: Failure simulation ENABLED");
    }

    /**
     * Disable failure simulation mode.
     */
    public void disableFailureSimulation() {
        this.simulateFailure = false;
        log.info("GPS Service: Failure simulation DISABLED");
    }

    /**
     * Enable slow response simulation.
     */
    public void enableSlowResponse() {
        this.simulateSlowResponse = true;
        log.info("GPS Service: Slow response simulation ENABLED");
    }

    /**
     * Disable slow response simulation.
     */
    public void disableSlowResponse() {
        this.simulateSlowResponse = false;
        log.info("GPS Service: Slow response simulation DISABLED");
    }
}
