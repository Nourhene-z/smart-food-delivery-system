package com.designpatterns.fooddelivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check and Info controller for application monitoring.
 *
 * Provides endpoints for application health and version information.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class HealthController {

    /**
     * Health check endpoint.
     *
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "Smart Food Delivery System");
        health.put("version", "1.0.0");

        return new ResponseEntity<>(health, HttpStatus.OK);
    }

    /**
     * Application info endpoint.
     *
     * @return application information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("application", "Smart Food Delivery System");
        info.put("version", "1.0.0");
        info.put("description", "Distributed food delivery backend demonstrating resilience and design patterns");
        info.put("java.version", System.getProperty("java.version"));
        info.put("spring.boot.version", "3.5.0");

        return new ResponseEntity<>(info, HttpStatus.OK);
    }
}
