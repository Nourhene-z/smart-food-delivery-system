package com.designpatterns.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main entry point for the Smart Food Delivery System application.
 *
 * This application demonstrates distributed system design patterns including:
 * - Circuit Breaker Pattern (Resilience4j)
 * - Retry Pattern (Resilience4j)
 * - Cache Pattern (Redis)
 * - Strategy Pattern (Payment Methods)
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableFeignClients
public class SmartFoodDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFoodDeliveryApplication.class, args);
    }
}
