package com.designpatterns.fooddelivery.controller;

import com.designpatterns.fooddelivery.dto.ApiResponse;
import com.designpatterns.fooddelivery.dto.CustomerDto;
import com.designpatterns.fooddelivery.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Customer Management endpoints.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Get customer by ID.
     *
     * @param customerId the customer ID
     * @return customer details
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomer(@PathVariable Long customerId) {
        log.info("GET /api/customers/{} - Fetching customer", customerId);
        CustomerDto customerDto = customerService.getCustomerById(customerId);
        ApiResponse<CustomerDto> response = ApiResponse.success(
                customerDto, "Customer retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get customer by email.
     *
     * @param email the customer email
     * @return customer details
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByEmail(@PathVariable String email) {
        log.info("GET /api/customers/email/{} - Fetching customer by email", email);
        CustomerDto customerDto = customerService.getCustomerByEmail(email);
        ApiResponse<CustomerDto> response = ApiResponse.success(
                customerDto, "Customer retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all customers.
     *
     * @return list of all customers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        log.info("GET /api/customers - Fetching all customers");
        List<CustomerDto> customers = customerService.getAllCustomers();
        ApiResponse<List<CustomerDto>> response = ApiResponse.success(
                customers, "Customers retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Create a new customer.
     *
     * @param customerDto the customer data
     * @return created customer
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(
            @RequestBody CustomerDto customerDto) {
        log.info("POST /api/customers - Creating new customer");
        CustomerDto created = customerService.createCustomer(customerDto);
        ApiResponse<CustomerDto> response = ApiResponse.success(
                created, "Customer created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update customer.
     *
     * @param customerId the customer ID
     * @param customerDto the updated customer data
     * @return updated customer
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody CustomerDto customerDto) {
        log.info("PUT /api/customers/{} - Updating customer", customerId);
        CustomerDto updated = customerService.updateCustomer(customerId, customerDto);
        ApiResponse<CustomerDto> response = ApiResponse.success(
                updated, "Customer updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete customer.
     *
     * @param customerId the customer ID
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<?>> deleteCustomer(@PathVariable Long customerId) {
        log.info("DELETE /api/customers/{} - Deleting customer", customerId);
        customerService.deleteCustomer(customerId);
        ApiResponse<?> response = ApiResponse.success(null, "Customer deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
