package com.designpatterns.fooddelivery.service;

import com.designpatterns.fooddelivery.dto.CustomerDto;
import com.designpatterns.fooddelivery.entity.Customer;
import com.designpatterns.fooddelivery.exception.ResourceNotFoundException;
import com.designpatterns.fooddelivery.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Customer operations.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Get customer by ID.
     *
     * @param customerId the customer ID
     * @return customer DTO
     * @throws ResourceNotFoundException if customer not found
     */
    public CustomerDto getCustomerById(Long customerId) {
        log.debug("Fetching customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));
        return convertToDto(customer);
    }

    /**
     * Get customer by email.
     *
     * @param email the customer email
     * @return customer DTO
     * @throws ResourceNotFoundException if customer not found
     */
    public CustomerDto getCustomerByEmail(String email) {
        log.debug("Fetching customer with email: {}", email);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with email: " + email));
        return convertToDto(customer);
    }

    /**
     * Get all customers.
     *
     * @return list of customers
     */
    public List<CustomerDto> getAllCustomers() {
        log.debug("Fetching all customers");
        return customerRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Create a new customer.
     *
     * @param customerDto the customer data
     * @return created customer DTO
     */
    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        log.info("Creating new customer with email: {}", customerDto.getEmail());

        // Check if customer already exists
        if (customerRepository.findByEmail(customerDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer already exists with email: " + customerDto.getEmail());
        }

        Customer customer = Customer.builder()
                .name(customerDto.getName())
                .email(customerDto.getEmail())
                .address(customerDto.getAddress())
                .phone(customerDto.getPhone())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with ID: {}", savedCustomer.getId());
        return convertToDto(savedCustomer);
    }

    /**
     * Update customer.
     *
     * @param customerId the customer ID
     * @param customerDto the updated customer data
     * @return updated customer DTO
     */
    @Transactional
    public CustomerDto updateCustomer(Long customerId, CustomerDto customerDto) {
        log.info("Updating customer: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));

        customer.setName(customerDto.getName());
        customer.setAddress(customerDto.getAddress());
        customer.setPhone(customerDto.getPhone());

        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDto(updatedCustomer);
    }

    /**
     * Delete customer.
     *
     * @param customerId the customer ID
     */
    @Transactional
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));

        customer.setIsActive(false);
        customerRepository.save(customer);
    }

    /**
     * Convert Customer entity to DTO.
     *
     * @param customer the customer entity
     * @return customer DTO
     */
    private CustomerDto convertToDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .phone(customer.getPhone())
                .isActive(customer.getIsActive())
                .registrationDate(customer.getRegistrationDate())
                .build();
    }
}
