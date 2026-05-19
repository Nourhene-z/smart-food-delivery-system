package com.designpatterns.fooddelivery.integration;

import com.designpatterns.fooddelivery.dto.CreateOrderRequest;
import com.designpatterns.fooddelivery.dto.OrderDto;
import com.designpatterns.fooddelivery.dto.PaymentRequest;
import com.designpatterns.fooddelivery.dto.PaymentResponse;
import com.designpatterns.fooddelivery.entity.Customer;
import com.designpatterns.fooddelivery.entity.Restaurant;
import com.designpatterns.fooddelivery.repository.CustomerRepository;
import com.designpatterns.fooddelivery.repository.OrderRepository;
import com.designpatterns.fooddelivery.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the Food Delivery System.
 *
 * Tests complete workflows including order creation, payment processing,
 * and delivery assignment.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
class FoodDeliveryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer testCustomer;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        // Create test customer
        testCustomer = Customer.builder()
                .name("Test Customer")
                .email("test@example.com")
                .address("123 Test St")
                .phone("1234567890")
                .build();
        testCustomer = customerRepository.save(testCustomer);

        // Create test restaurant
        testRestaurant = Restaurant.builder()
                .name("Test Restaurant")
                .category("Indian")
                .description("Test Description")
                .address("456 Restaurant Ave")
                .phone("9876543210")
                .rating(4.5)
                .build();
        testRestaurant = restaurantRepository.save(testRestaurant);
    }

    @Test
    void testCompleteOrderWorkflow() throws Exception {
        // 1. Create an order
        CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .customerId(testCustomer.getId())
                .restaurantId(testRestaurant.getId())
                .totalAmount(50.0)
                .deliveryAddress("123 Test St")
                .specialInstructions("No spice")
                .paymentMethod("CREDIT_CARD")
                .build();

        String createOrderResponse = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract order ID from response
        Long orderId = objectMapper.readTree(createOrderResponse)
                .get("data")
                .get("id")
                .asLong();

        // 2. Verify order was created
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        // 3. Process payment
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .amount(50.0)
                .paymentMethod("CREDIT_CARD")
                .build();

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.success").value(true));

        // 4. Assign delivery
        mockMvc.perform(post("/api/delivery/{orderId}/assign", orderId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void testRestaurantCaching() throws Exception {
        // First call - should fetch from database
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Second call - should use cache
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetTopRatedRestaurants() throws Exception {
        mockMvc.perform(get("/api/restaurants/top-rated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testCreateCustomer() throws Exception {
        Customer newCustomer = Customer.builder()
                .name("New Customer")
                .email("newcustomer@example.com")
                .address("999 New St")
                .phone("5555555555")
                .build();

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("newcustomer@example.com"));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Smart Food Delivery System"));
    }

    @Test
    void testApplicationInfo() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("Smart Food Delivery System"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }
}
