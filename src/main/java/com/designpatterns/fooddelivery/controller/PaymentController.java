package com.designpatterns.fooddelivery.controller;

import com.designpatterns.fooddelivery.dto.ApiResponse;
import com.designpatterns.fooddelivery.dto.PaymentRequest;
import com.designpatterns.fooddelivery.dto.PaymentResponse;
import com.designpatterns.fooddelivery.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Payment Processing endpoints.
 *
 * Demonstrates the Strategy Pattern for payment processing.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a payment for an order.
     *
     * This endpoint demonstrates the Strategy Pattern by dynamically
     * selecting the appropriate payment processing strategy based on
     * the payment method.
     *
     * @param request the payment request containing order ID, amount, and payment method
     * @return payment response with transaction details
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        log.info("POST /api/payments/process - Processing payment for Order: {} using method: {}",
                request.getOrderId(), request.getPaymentMethod());

        PaymentResponse paymentResponse = paymentService.processPayment(request);
        ApiResponse<PaymentResponse> response = ApiResponse.success(
                paymentResponse, "Payment processed successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Refund a payment for an order.
     *
     * @param orderId the order ID
     * @param amount the refund amount
     * @return refund response
     */
    @PostMapping("/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @RequestParam Long orderId,
            @RequestParam Double amount) {
        log.info("POST /api/payments/refund - Refunding Order: {}, Amount: {}", orderId, amount);

        PaymentResponse refundResponse = paymentService.refundPayment(orderId, amount);
        ApiResponse<PaymentResponse> response = ApiResponse.success(
                refundResponse, "Refund processed successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
