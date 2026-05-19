package com.designpatterns.fooddelivery.service;

import com.designpatterns.fooddelivery.dto.RestaurantDto;
import com.designpatterns.fooddelivery.entity.Restaurant;
import com.designpatterns.fooddelivery.exception.ResourceNotFoundException;
import com.designpatterns.fooddelivery.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Restaurant operations.
 *
 * Implements caching for frequently accessed restaurant data to demonstrate
 * the Cache Pattern.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    /**
     * Get all active restaurants with caching.
     * Results are cached for improved performance.
     *
     * @return list of active restaurants
     */
    @Cacheable(value = "restaurants", unless = "#result.isEmpty()")
    public List<RestaurantDto> getAllRestaurants() {
        log.debug("Fetching all active restaurants");
        return restaurantRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Get top-rated restaurants with caching.
     * This method demonstrates Redis caching of expensive queries.
     *
     * @return list of top-rated restaurants
     */
    @Cacheable(value = "topRatedRestaurants", unless = "#result.isEmpty()")
    public List<RestaurantDto> getTopRatedRestaurants() {
        log.debug("Fetching top-rated restaurants");
        return restaurantRepository.findTopRated(PageRequest.of(0, 10))
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Get restaurants by category with caching.
     *
     * @param category the restaurant category
     * @return list of restaurants in the specified category
     */
    @Cacheable(value = "restaurantsByCategory", key = "#category")
    public List<RestaurantDto> getRestaurantsByCategory(String category) {
        log.debug("Fetching restaurants for category: {}", category);
        return restaurantRepository.findByCategory(category)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Get restaurant by ID.
     *
     * @param restaurantId the restaurant ID
     * @return restaurant DTO
     * @throws ResourceNotFoundException if restaurant not found
     */
    public RestaurantDto getRestaurantById(Long restaurantId) {
        log.debug("Fetching restaurant with ID: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with ID: " + restaurantId));
        return convertToDto(restaurant);
    }

    /**
     * Create a new restaurant.
     *
     * @param restaurantDto the restaurant data
     * @return created restaurant DTO
     */
    public RestaurantDto createRestaurant(RestaurantDto restaurantDto) {
        log.info("Creating new restaurant: {}", restaurantDto.getName());
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .category(restaurantDto.getCategory())
                .description(restaurantDto.getDescription())
                .address(restaurantDto.getAddress())
                .phone(restaurantDto.getPhone())
                .rating(restaurantDto.getRating())
                .build();

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created with ID: {}", savedRestaurant.getId());
        return convertToDto(savedRestaurant);
    }

    /**
     * Convert Restaurant entity to DTO.
     *
     * @param restaurant the restaurant entity
     * @return restaurant DTO
     */
    private RestaurantDto convertToDto(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .rating(restaurant.getRating())
                .isActive(restaurant.getIsActive())
                .createdAt(restaurant.getCreatedAt())
                .build();
    }
}
