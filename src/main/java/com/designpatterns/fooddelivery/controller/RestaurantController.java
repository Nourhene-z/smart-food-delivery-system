package com.designpatterns.fooddelivery.controller;

import com.designpatterns.fooddelivery.dto.ApiResponse;
import com.designpatterns.fooddelivery.dto.RestaurantDto;
import com.designpatterns.fooddelivery.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Restaurant Management endpoints.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * Get all restaurants.
     * Results are cached for improved performance.
     *
     * @return list of all restaurants
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantDto>>> getAllRestaurants() {
        log.info("GET /api/restaurants - Fetching all restaurants");
        List<RestaurantDto> restaurants = restaurantService.getAllRestaurants();
        ApiResponse<List<RestaurantDto>> response = ApiResponse.success(
                restaurants, "Restaurants retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get top-rated restaurants.
     * Results are cached for improved performance.
     *
     * @return list of top-rated restaurants
     */
    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<RestaurantDto>>> getTopRatedRestaurants() {
        log.info("GET /api/restaurants/top-rated - Fetching top-rated restaurants");
        List<RestaurantDto> restaurants = restaurantService.getTopRatedRestaurants();
        ApiResponse<List<RestaurantDto>> response = ApiResponse.success(
                restaurants, "Top-rated restaurants retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get restaurants by category.
     *
     * @param category the restaurant category
     * @return list of restaurants in the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<RestaurantDto>>> getRestaurantsByCategory(
            @PathVariable String category) {
        log.info("GET /api/restaurants/category/{} - Fetching restaurants by category", category);
        List<RestaurantDto> restaurants = restaurantService.getRestaurantsByCategory(category);
        ApiResponse<List<RestaurantDto>> response = ApiResponse.success(
                restaurants, "Restaurants retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get restaurant by ID.
     *
     * @param restaurantId the restaurant ID
     * @return restaurant details
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<ApiResponse<RestaurantDto>> getRestaurantById(
            @PathVariable Long restaurantId) {
        log.info("GET /api/restaurants/{} - Fetching restaurant", restaurantId);
        RestaurantDto restaurant = restaurantService.getRestaurantById(restaurantId);
        ApiResponse<RestaurantDto> response = ApiResponse.success(
                restaurant, "Restaurant retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Create a new restaurant.
     *
     * @param restaurantDto the restaurant data
     * @return created restaurant
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantDto>> createRestaurant(
            @RequestBody RestaurantDto restaurantDto) {
        log.info("POST /api/restaurants - Creating new restaurant");
        RestaurantDto created = restaurantService.createRestaurant(restaurantDto);
        ApiResponse<RestaurantDto> response = ApiResponse.success(
                created, "Restaurant created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
