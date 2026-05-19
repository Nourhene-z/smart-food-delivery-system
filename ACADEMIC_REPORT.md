# Smart Food Delivery System
## Design Patterns Implementation Report

**Resilience Patterns for Distributed Food Delivery Services**

---

**Author:** Nourhene  
**Date:** May 2026  
**Repository:** [github.com/Nourhene-z/smart-food-delivery-system](https://github.com/Nourhene-z/smart-food-delivery-system)  
**Technology Stack:** Spring Boot 3.5 · Resilience4j 2.1 · Spring Cloud OpenFeign · Redis 7.0 · MySQL 8.0

---

## Contents

1. [Introduction](#1-introduction)
2. [Architecture](#2-architecture)
   - [Request Flow](#21-request-flow)
   - [Package Structure](#22-package-structure)
3. [Pattern 1 — Circuit Breaker](#3-pattern-1--circuit-breaker)
   - [Concept](#31-concept)
   - [State Machine](#32-state-machine)
   - [Configuration](#33-configuration)
   - [Implementation](#34-implementation)
4. [Pattern 2 — Retry](#4-pattern-2--retry)
   - [Concept](#41-concept)
   - [Backoff Strategy](#42-backoff-strategy)
   - [Configuration](#43-configuration)
   - [Implementation](#44-implementation)
5. [Pattern 3 — Cache](#5-pattern-3--cache)
   - [Concept](#51-concept)
   - [Cache Strategy](#52-cache-strategy)
   - [Configuration](#53-configuration)
   - [Implementation](#54-implementation)
6. [Pattern 4 — Strategy](#6-pattern-4--strategy)
   - [Concept](#61-concept)
   - [Strategy Implementations](#62-strategy-implementations)
   - [Factory Pattern](#63-factory-pattern)
   - [Implementation](#64-implementation)
7. [REST API](#7-rest-api)
   - [Core Endpoints](#71-core-endpoints)
   - [Observability Endpoints](#72-observability-endpoints)
8. [Mock External Services](#8-mock-external-services)
9. [Demo Scenarios](#9-demo-scenarios)
   - [Running the Application](#91-running-the-application)
   - [Demo 1 — Circuit Breaker Activation](#92-demo-1--circuit-breaker-activation)
   - [Demo 2 — Retry with Backoff](#93-demo-2--retry-with-backoff)
   - [Demo 3 — Cache Effectiveness](#94-demo-3--cache-effectiveness)
   - [Demo 4 — Strategy Selection](#95-demo-4--strategy-selection)
10. [Technology Stack](#10-technology-stack)
11. [Key Design Decisions](#11-key-design-decisions)
12. [Conclusion](#12-conclusion)

---

## 1. Introduction

Modern distributed systems routinely depend on external services — payment gateways, GPS tracking APIs, third-party delivery platforms, and databases. When a downstream service degrades, fails entirely, or becomes slow, naive client implementations cause failures to cascade through the call chain, ultimately bringing down the entire system.

This project is a **self-contained Spring Boot 3.5 application** that demonstrates four complementary, production-grade design patterns optimized for resilience and maintainability:

| Pattern | Problem it Solves |
|---------|------------------|
| **Circuit Breaker** | Stops hammering a failing service. After the failure rate crosses a threshold, the circuit opens and further calls fail immediately (fast-fail) without reaching the downstream service. |
| **Retry** | Automatically recovers from transient failures using configurable exponential backoff and exception filtering. |
| **Cache** | Reduces database load and improves response time for frequently accessed data (restaurants, ratings, recommendations). |
| **Strategy** | Enables runtime selection of payment methods and algorithms without coupling business logic to specific implementations. |

All patterns are applied **declaratively** via Resilience4j and Spring annotations with zero framework coupling in the business logic layer.

---

## 2. Architecture

### 2.1 Request Flow

A typical food delivery workflow passes through a layered resilience chain:

```
HTTP Client POST /api/orders
    ↓
OrderController
    ↓
OrderService (orchestration layer)
    ├── CustomerRepository (cached queries)
    ├── RestaurantRepository (Redis cache)
    ├── PaymentService
    │   ├── CircuitBreaker
    │   ├── Strategy (payment method selection)
    │   └── MockPaymentGateway
    │
    ├── DeliveryService
    │   ├── CircuitBreaker (GPS service)
    │   ├── Retry (with backoff)
    │   └── GpsServiceClient (Feign)
    │       └── MockGpsService
    │
    └── NotificationService (async events)
    
Response ApiResponse<OrderDto>
```

**Resilience4j applies decorators from outermost to innermost:**

1. **Circuit Breaker** — Evaluated first (outermost), protects against cascading failures
2. **Retry** — Evaluated second, automatically retries with backoff
3. **Business Logic** — Core service method execution
4. **External Calls** — Feign HTTP clients or database queries

### 2.2 Package Structure

```
src/main/java/com/designpatterns/fooddelivery/
├── config/                          # Spring configuration
│   ├── AppConfig.java               # Caching & transaction setup
│   └── HttpClientConfig.java        # RestTemplate & Feign config
│
├── controller/                      # REST API endpoints
│   ├── OrderController.java         # Order management
│   ├── RestaurantController.java    # Restaurant browsing
│   ├── PaymentController.java       # Payment processing
│   ├── DeliveryController.java      # Delivery tracking
│   ├── CustomerController.java      # Customer management
│   └── HealthController.java        # Health & observability
│
├── service/                         # Business logic layer
│   ├── OrderService.java            # Order orchestration
│   ├── RestaurantService.java       # @Cacheable queries
│   ├── PaymentService.java          # Strategy pattern + Circuit Breaker
│   ├── DeliveryService.java         # Circuit Breaker + Retry
│   └── CustomerService.java         # Customer operations
│
├── repository/                      # Data access layer
│   ├── OrderRepository.java         # JPA repository
│   ├── RestaurantRepository.java    # Custom queries with @Query
│   ├── DeliveryRepository.java      # Delivery persistence
│   └── CustomerRepository.java      # Customer lookups
│
├── entity/                          # JPA entities
│   ├── Order.java                   # Order with enum status
│   ├── Restaurant.java              # Restaurant with rating
│   ├── Delivery.java                # Delivery with GPS coords
│   └── Customer.java                # Customer profile
│
├── dto/                             # Data transfer objects
│   ├── ApiResponse.java             # Generic response wrapper
│   ├── OrderDto.java                # Order DTO
│   ├── RestaurantDto.java           # Restaurant DTO
│   ├── DeliveryDto.java             # Delivery DTO
│   ├── PaymentRequest.java          # Payment input
│   ├── PaymentResponse.java         # Payment output
│   ├── CreateOrderRequest.java      # Order creation input
│   └── CustomerDto.java             # Customer DTO
│
├── strategy/                        # Strategy pattern
│   ├── PaymentStrategy.java         # Strategy interface
│   ├── CreditCardPayment.java       # Credit card impl (95% success)
│   ├── PayPalPayment.java           # PayPal impl (97% success)
│   ├── CashPayment.java             # Cash impl (100% success)
│   └── PaymentStrategyFactory.java  # Factory for strategy selection
│
├── client/                          # HTTP clients
│   └── GpsServiceClient.java        # Feign client w/ CB + Retry
│
├── mock/                            # Mock external services
│   ├── MockGpsService.java          # GPS simulation
│   ├── MockPaymentGateway.java      # Payment simulation
│   └── MockNotificationService.java # Notification simulation
│
├── exception/                       # Error handling
│   ├── ResourceNotFoundException.java
│   ├── PaymentException.java
│   └── GlobalExceptionHandler.java  # @RestControllerAdvice
│
└── SmartFoodDeliveryApplication.java  # Main entry point
```

---

## 3. Pattern 1 — Circuit Breaker

### 3.1 Concept

The Circuit Breaker pattern is named after the electrical analogue: when a fault occurs, the breaker trips, cutting current until the fault resolves. In software, the Circuit Breaker wraps outgoing calls and monitors the failure rate over a sliding window. Once the failure rate exceeds a configured threshold, the circuit opens and all further calls are rejected immediately — no network round-trip is made — until the downstream service recovers.

**Real-world scenario:** When the external GPS tracking service is overloaded (e.g., during peak delivery hours), the Circuit Breaker prevents the delivery system from repeatedly attempting to contact it, instead returning cached delivery data and allowing the GPS service time to recover.

### 3.2 State Machine

```
    ┌─────────┐
    │ CLOSED  │ ← All calls pass through; monitor failure rate
    │         │
    └────┬────┘
         │ failure rate ≥ 50%
         ↓
    ┌──────────┐
    │   OPEN   │ ← All calls rejected immediately (fast-fail)
    │          │
    └────┬─────┘
         │ after 30 s
         ↓
    ┌────────────┐
    │ HALF_OPEN  │ ← Allow 3 probe calls
    │            │
    └────┬────┬──┘
         │    │
    all pass  any fails
         │    │
         ↓    ↓
      CLOSED  OPEN
```

| State | Behaviour |
|-------|-----------|
| **CLOSED** | All calls pass through; failure rate is tracked over a 100-call sliding window. |
| **OPEN** | All calls fail immediately with a Circuit Breaker exception; the downstream service is never contacted. Reduces load on the failing service. |
| **HALF_OPEN** | 3 probe calls are allowed. If all succeed → CLOSED. If any fail → OPEN. |

### 3.3 Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      gpsServiceCircuitBreaker:
        sliding-window-type: COUNT_BASED           # evaluate last N calls
        sliding-window-size: 100                   # N = 100 calls
        failure-rate-threshold: 50                 # open when ≥ 50% fail
        wait-duration-in-open-state: 30000         # stay OPEN for 30 s
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        register-health-indicator: true
        event-consumer-buffer-size: 10
```

### 3.4 Implementation

**DeliveryService.java:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final GpsServiceClient gpsServiceClient;
    
    /**
     * Retrieves delivery with real-time GPS location.
     * Protected by Circuit Breaker and Retry patterns.
     */
    @CircuitBreaker(name = "gpsServiceCircuitBreaker", 
                   fallbackMethod = "getDeliveryFallback")
    @Retry(name = "deliveryTrackingRetry")
    @Transactional(readOnly = true)
    public DeliveryDto getDeliveryWithLocation(Long deliveryId) {
        log.info("Fetching delivery location for ID: {}", deliveryId);
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Delivery not found: " + deliveryId));
        
        // Call external GPS service (protected by Circuit Breaker + Retry)
        DeliveryDto dto = gpsServiceClient.getDeliveryLocation(deliveryId);
        
        // Update with latest coordinates
        delivery.setCurrentLatitude(dto.getCurrentLatitude());
        delivery.setCurrentLongitude(dto.getCurrentLongitude());
        
        return convertToDto(delivery);
    }
    
    /**
     * Fallback when Circuit Breaker is OPEN.
     * Returns cached delivery data without contacting GPS service.
     */
    private DeliveryDto getDeliveryFallback(Long deliveryId, 
                                            Exception ex) {
        log.warn("[CIRCUIT_BREAKER] GPS service unavailable. " +
                 "Returning cached delivery data for ID: {}", 
                 deliveryId);
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Delivery not found: " + deliveryId));
        
        // Return delivery with last known location (cached)
        return convertToDto(delivery);
    }
}
```

**GpsServiceClient.java (Feign):**

```java
@FeignClient(name = "gpsService", url = "http://localhost:8081/api/gps")
public interface GpsServiceClient {
    
    @GetMapping("/{deliveryId}")
    @CircuitBreaker(name = "gpsServiceCircuitBreaker")
    @Retry(name = "deliveryTrackingRetry")
    DeliveryDto getDeliveryLocation(@PathVariable Long deliveryId);
}
```

---

## 4. Pattern 2 — Retry

### 4.1 Concept

The Retry pattern automatically reattempts failed operations without manual intervention. Unlike blind retries, production-grade implementations use:

- **Exponential backoff** — Waits grow between attempts (1s, 2s, 4s)
- **Exception filtering** — Only retry on transient errors (timeouts, network errors), not on permanent failures (401, 404)
- **Max attempts limit** — Prevents resource exhaustion

**Real-world scenario:** Network hiccups or temporary service slowness cause 5% of delivery location updates to fail. Retrying with 2-second backoff recovers most transient failures transparently.

### 4.2 Backoff Strategy

```
Attempt 1: Fail → wait 2s
    ↓
Attempt 2: Fail → wait 2s
    ↓
Attempt 3: Fail → throw exception
```

After 3 total attempts with 2-second waits between them, the request either succeeds or throws an exception that the Circuit Breaker can capture.

### 4.3 Configuration

```yaml
resilience4j:
  retry:
    instances:
      deliveryTrackingRetry:
        max-attempts: 3                    # total attempts including first
        wait-duration: 2000                # 2 seconds between attempts
        retry-exceptions:                  # only retry on these
          - java.net.ConnectException
          - java.net.SocketTimeoutException
          - java.io.IOException
        ignore-exceptions:
          - com.designpatterns.fooddelivery.exception.PaymentException
```

### 4.4 Implementation

**DeliveryService.java:**

```java
@CircuitBreaker(name = "gpsServiceCircuitBreaker", 
               fallbackMethod = "getDeliveryFallback")
@Retry(name = "deliveryTrackingRetry")  // Retry applied second (inner)
@Transactional(readOnly = true)
public DeliveryDto getDeliveryWithLocation(Long deliveryId) {
    log.info("Fetching delivery location (with retry) for ID: {}", 
             deliveryId);
    
    // If this call times out or throws IOException, 
    // it will be retried up to 3 times with 2s backoff
    return gpsServiceClient.getDeliveryLocation(deliveryId);
}
```

**Retry decorator behavior:**

- **Attempt 1 fails** (SocketTimeoutException) → Wait 2s
- **Attempt 2 fails** (SocketTimeoutException) → Wait 2s
- **Attempt 3 succeeds** → Return result immediately
- **Result:** Request succeeds without Circuit Breaker intervention

---

## 5. Pattern 3 — Cache

### 5.1 Concept

The Cache pattern stores frequently accessed data in memory (or a distributed cache like Redis), reducing expensive database queries and improving response times. Cached data is invalidated after a configured TTL (Time To Live), ensuring freshness.

**Real-world scenario:** Restaurant lists are requested 1000s of times per day but change infrequently. Caching restaurants in Redis with a 1-hour TTL reduces database load by 99% while serving data in < 5ms.

### 5.2 Cache Strategy

```
Request 1: GET /api/restaurants
    ↓ (Cache MISS)
Query MySQL Database
    ↓
Return results + store in Redis
    ↓
Response time: ~200ms

Request 2 (within 1 hour): GET /api/restaurants
    ↓ (Cache HIT)
Fetch from Redis (no DB query)
    ↓
Response time: ~5ms
```

### 5.3 Configuration

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1 hour in milliseconds
  redis:
    host: localhost
    port: 6379
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
```

### 5.4 Implementation

**RestaurantService.java:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    
    /**
     * Returns all active restaurants.
     * Results cached in Redis with 1-hour TTL.
     * Unless result is empty (skip cache for empty results).
     */
    @Cacheable(value = "restaurants", unless = "#result.isEmpty()")
    public List<RestaurantDto> getAllRestaurants() {
        log.info("Fetching all restaurants from database (cache miss)");
        
        return restaurantRepository.findByIsActiveTrue()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Returns top 10 rated restaurants.
     * Cached separately from getAllRestaurants.
     */
    @Cacheable(value = "topRatedRestaurants")
    public List<RestaurantDto> getTopRatedRestaurants() {
        log.info("Fetching top-rated restaurants");
        
        return restaurantRepository
            .findTopRated(PageRequest.of(0, 10))
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Returns restaurants by category.
     * Cached per category (using SpEL key).
     */
    @Cacheable(value = "restaurantsByCategory", key = "#category")
    public List<RestaurantDto> getRestaurantsByCategory(String category) {
        log.info("Fetching restaurants in category: {}", category);
        
        return restaurantRepository.findByCategory(category)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
}
```

**Cache monitoring:**

```bash
# Check cache status
curl http://localhost:8080/actuator/caches

# Response includes cache names, size, hits, misses
```

---

## 6. Pattern 4 — Strategy

### 6.1 Concept

The Strategy pattern encapsulates a family of algorithms, making them interchangeable. A context object selects the appropriate strategy at runtime without the caller knowing the specific implementation.

**Real-world scenario:** Users can pay with Credit Card (95% success), PayPal (97% success), or Cash (100% success). The payment service doesn't hardcode payment logic; instead, it selects the right strategy based on the user's choice.

### 6.2 Strategy Implementations

| Strategy | Success Rate | Use Case |
|----------|-------------|----------|
| **CreditCardPayment** | 95% | Online payment with fraud detection |
| **PayPalPayment** | 97% | Third-party wallet with buyer protection |
| **CashPayment** | 100% | Cash-on-delivery (no network risk) |

Each strategy generates a unique transaction ID:
- Credit Card: `CC-{timestamp}`
- PayPal: `PP-{timestamp}`
- Cash: `CASH-{timestamp}`

### 6.3 Factory Pattern

```java
public class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategies;
    
    public PaymentStrategy getStrategy(String paymentMethod) {
        switch (paymentMethod.toUpperCase()) {
            case "CREDIT_CARD":
            case "CREDITCARD":
            case "CC":
                return strategies.get("creditCardPayment");
            case "PAYPAL":
            case "PP":
                return strategies.get("payPalPayment");
            case "CASH":
                return strategies.get("cashPayment");
            default:
                throw new PaymentException(
                    "Unsupported payment method: " + paymentMethod);
        }
    }
}
```

### 6.4 Implementation

**PaymentStrategy.java (Interface):**

```java
public interface PaymentStrategy {
    boolean processPayment(Long orderId, Double amount);
    boolean refundPayment(Long orderId, Double amount);
    String getPaymentMethodName();
}
```

**CreditCardPayment.java:**

```java
@Component("creditCardPayment")
@Slf4j
public class CreditCardPayment implements PaymentStrategy {
    private static final double SUCCESS_RATE = 0.95;  // 95%
    private final Random random = new Random();
    
    @Override
    public boolean processPayment(Long orderId, Double amount) {
        log.info("[CC] Processing credit card payment for order: {}", orderId);
        
        // Simulate 95% success rate
        if (random.nextDouble() > (1.0 - SUCCESS_RATE)) {
            log.info("[CC] ✓ Payment approved for order: {}", orderId);
            return true;
        } else {
            log.warn("[CC] ✗ Payment declined for order: {}", orderId);
            return false;
        }
    }
    
    @Override
    public String getPaymentMethodName() {
        return "CREDIT_CARD";
    }
}
```

**PaymentService.java (Context):**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final OrderRepository orderRepository;
    private final MockPaymentGateway mockPaymentGateway;
    
    /**
     * Processes payment using the selected strategy.
     * The strategy is chosen at runtime based on paymentMethod.
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment: {}", request.getPaymentMethod());
        
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Order not found"));
        
        // Select strategy dynamically
        PaymentStrategy strategy = 
            paymentStrategyFactory.getStrategy(request.getPaymentMethod());
        
        // Mock payment gateway call
        boolean gatewayApproval = 
            mockPaymentGateway.processPayment(
                request.getOrderId(), 
                request.getAmount());
        
        // Execute strategy
        boolean strategyApproval = strategy.processPayment(
            request.getOrderId(), 
            request.getAmount());
        
        if (gatewayApproval && strategyApproval) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            
            return PaymentResponse.builder()
                .orderId(request.getOrderId())
                .success(true)
                .transactionId(strategy.getPaymentMethodName() + 
                              "-" + System.currentTimeMillis())
                .paymentStatus("APPROVED")
                .build();
        }
        
        return PaymentResponse.builder()
            .orderId(request.getOrderId())
            .success(false)
            .paymentStatus("DECLINED")
            .message("Payment processing failed")
            .build();
    }
}
```

---

## 7. REST API

### 7.1 Core Endpoints

#### Orders
```
POST /api/orders
    Create new order with validation

GET /api/orders/{id}
    Retrieve order by ID

GET /api/orders/customer/{customerId}
    List all orders for a customer

PUT /api/orders/{id}/status
    Update order status (query param: status=CONFIRMED)

DELETE /api/orders/{id}
    Cancel order
```

#### Restaurants (Cached)
```
GET /api/restaurants
    All restaurants (Redis cached, 1-hour TTL)

GET /api/restaurants/top-rated
    Top 10 restaurants (separate cache)

GET /api/restaurants/category/{category}
    Restaurants by category (cached per category)

POST /api/restaurants
    Create restaurant

GET /api/restaurants/{id}
    Get restaurant details
```

#### Payments (Strategy Pattern)
```
POST /api/payments/process
    Process payment (supports CREDIT_CARD, PAYPAL, CASH)
    Strategy selected at runtime based on paymentMethod

POST /api/payments/refund
    Process refund
```

#### Delivery (Circuit Breaker + Retry)
```
POST /api/delivery/{orderId}/assign
    Assign delivery driver

GET /api/delivery/{id}
    Get delivery with GPS location (Circuit Breaker protected)

GET /api/delivery/order/{orderId}
    Get delivery by order ID

PUT /api/delivery/{id}/status
    Update delivery status

GET /api/delivery/active
    List active deliveries
```

### 7.2 Observability Endpoints

```
GET /api/health
    Application health

GET /api/info
    Application information

GET /actuator/health
    Detailed health with Circuit Breaker state

GET /actuator/circuitbreakers
    Circuit Breaker metrics and state

GET /actuator/retries
    Retry metrics

GET /actuator/caches
    Cache statistics

GET /actuator/metrics
    All metrics
```

**Example response — Circuit Breaker metrics:**

```json
{
  "circuitbreakers": [
    {
      "name": "gpsServiceCircuitBreaker",
      "state": "CLOSED",
      "buffered-calls": 87,
      "failed-calls": 3,
      "successful-calls": 84,
      "not-permitted-calls": 0,
      "failure-rate": 3.45
    }
  ]
}
```

---

## 8. Mock External Services

The project includes three in-process mock services that simulate external dependencies:

### MockGpsService
- Simulates GPS tracking with random coordinates
- Supports controllable failure modes:
  - `simulateFailure = true` → 100% failure rate
  - `simulateSlowResponse = true` → 8-second delay
- Default: 10% natural failure rate

### MockPaymentGateway
- Simulates payment processing
- Built-in failure modes:
  - 2% timeout rate
  - 3% network errors
  - 2% payment decline rate

### MockNotificationService
- Simulates notification delivery
- 2% failure rate for testing resilience

---

## 9. Demo Scenarios

### 9.1 Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Server starts on http://localhost:8080
```

### 9.2 Demo 1 — Circuit Breaker Activation

**Objective:** Trigger the Circuit Breaker by forcing GPS service failures

```bash
# Step 1: Enable GPS service failure mode
curl -X POST http://localhost:8080/mock/gateway/control/failure?enabled=true

# Step 2: Send 10 delivery location requests to fill sliding window
for i in {1..10}; do
  curl -s -X GET http://localhost:8080/api/delivery/$i \
    -H "Content-Type: application/json"
  echo ""
done

# Step 3: Check Circuit Breaker state (should be OPEN)
curl http://localhost:8080/actuator/circuitbreakers | jq

# Expected: Circuit Breaker state = "OPEN"
#           Last 5 requests returned immediately (no GPS contact)

# Step 4: New delivery request returns CIRCUIT_OPEN instantly
curl -s -X GET http://localhost:8080/api/delivery/999

# Step 5: Re-enable GPS service; CB auto-transitions to HALF_OPEN
curl -X POST http://localhost:8080/mock/gateway/control/failure?enabled=false

# Wait 30 seconds for automatic transition

# Step 6: CB transitions: HALF_OPEN → CLOSED (if probes succeed)
curl http://localhost:8080/actuator/circuitbreakers | jq
```

**Expected observations:**
1. After 5 failures in 100 calls (5% failure rate crosses 50% threshold), circuit opens
2. Subsequent calls return immediately with fallback data (no GPS contact)
3. After 30 seconds, circuit transitions to HALF_OPEN
4. 3 probe calls succeed → circuit closes
5. Normal operation resumes

### 9.3 Demo 2 — Retry with Backoff

**Objective:** Demonstrate automatic retry on transient failures

```bash
# Step 1: Enable slow response mode (simulates network lag)
curl -X POST http://localhost:8080/mock/gateway/control/slow?enabled=true

# Step 2: Send delivery location request with retry
curl -X GET http://localhost:8080/api/delivery/1

# Watch logs for retry output:
# [Attempt 1] SocketTimeoutException → wait 2s
# [Attempt 2] Success → return result

# Expected: Request succeeds after 1-2 retries

# Step 3: Disable slow mode
curl -X POST http://localhost:8080/mock/gateway/control/slow?enabled=false
```

**Expected observations:**
1. First request times out → Retry kicks in
2. Wait 2 seconds
3. Second attempt succeeds
4. Result returned to client

### 9.4 Demo 3 — Cache Effectiveness

**Objective:** Demonstrate cache hits reducing database queries

```bash
# Step 1: First request (cache miss — hits database)
time curl -s http://localhost:8080/api/restaurants | jq . | head -20

# Expected response time: ~150-200ms (database query)
# Cache miss shown in logs

# Step 2: Second request (cache hit — Redis)
time curl -s http://localhost:8080/api/restaurants | jq . | head -20

# Expected response time: ~5-10ms (Redis lookup)
# Cache hit shown in logs

# Step 3: Check cache statistics
curl http://localhost:8080/actuator/caches | jq '.caches.restaurants'

# Step 4: Try different categories (separate cache keys)
curl http://localhost:8080/api/restaurants/category/Italian
curl http://localhost:8080/api/restaurants/category/Italian  # cache hit
```

**Expected observations:**
1. First request hits database (~200ms)
2. Second request returns from Redis (~5ms)
3. Cache statistics show hits and misses
4. Each category cached separately (key-based caching)

### 9.5 Demo 4 — Strategy Selection

**Objective:** Demonstrate dynamic payment method selection

```bash
# Create order first
ORDER_ID=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": 1, "restaurantId": 1, "totalAmount": 50.0, 
       "deliveryAddress": "123 Main St", "paymentMethod": "CREDIT_CARD"}' \
  | jq -r '.data.id')

# Demo 1: Credit Card Payment (95% success)
for i in {1..5}; do
  curl -s -X POST http://localhost:8080/api/payments/process \
    -H "Content-Type: application/json" \
    -d "{\"orderId\": $ORDER_ID, \"amount\": 50.0, 
         \"paymentMethod\": \"CREDIT_CARD\"}" | jq '.paymentStatus'
done

# Demo 2: PayPal Payment (97% success)
curl -s -X POST http://localhost:8080/api/payments/process \
  -H "Content-Type: application/json" \
  -d "{\"orderId\": $ORDER_ID, \"amount\": 50.0, 
       \"paymentMethod\": \"PAYPAL\"}" | jq '.'

# Demo 3: Cash Payment (100% success)
curl -s -X POST http://localhost:8080/api/payments/process \
  -H "Content-Type: application/json" \
  -d "{\"orderId\": $ORDER_ID, \"amount\": 50.0, 
       \"paymentMethod\": \"CASH\"}" | jq '.'

# Observe in logs:
# [CC] Processing credit card payment...
# [PP] Processing PayPal payment...
# [CASH] Processing cash payment...
```

**Expected observations:**
1. Each payment method logs different processing logic
2. Success rates match strategy implementation (CC: 95%, PP: 97%, Cash: 100%)
3. Transaction ID format differs by strategy (CC-..., PP-..., CASH-...)
4. Strategy selected at runtime based on paymentMethod parameter

---

## 10. Technology Stack

| Dependency | Role | Version |
|-----------|------|---------|
| **Spring Boot** | Application framework | 3.5.0 |
| **Spring Data JPA** | ORM and repositories | 3.5.0 |
| **Spring Cloud OpenFeign** | Declarative HTTP client | 4.1.0 |
| **Resilience4j** | Circuit Breaker, Retry | 2.1.0 |
| **MySQL** | Primary database | 8.0 |
| **Redis** | Distributed cache | 7.0 |
| **Spring Cache Abstraction** | Cache management | 3.5.0 |
| **Lombok** | Boilerplate reduction | Latest |
| **Spring Boot Actuator** | Health & metrics endpoints | 3.5.0 |
| **JUnit 5** | Testing framework | Latest |
| **Mockito** | Mocking library | Latest |
| **Docker** | Containerization | Latest |

---

## 11. Key Design Decisions

1. **Annotation-driven resilience patterns**
   - Both `@CircuitBreaker` and `@Retry` are applied as pure annotations
   - Service methods remain simple delegates with zero framework-coupling
   - Resilience logic is completely decoupled from business logic
   - Easy to enable/disable patterns without code changes

2. **Redis for distributed caching**
   - Supports multi-instance deployments
   - Persistent cache survives application restarts
   - Configurable TTL ensures data freshness
   - Built-in eviction policies prevent memory exhaustion

3. **Strategy pattern for payment flexibility**
   - New payment methods can be added without modifying existing code (Open/Closed Principle)
   - Runtime selection allows A/B testing different payment methods
   - Each strategy has clear success rate characteristics
   - Factory pattern centralizes strategy creation logic

4. **In-process mock services**
   - No external infrastructure required for demos
   - Deterministic simulation of failures and delays
   - Realistic Feign HTTP calls over localhost
   - Full self-contained learning environment

5. **Decorator composition (Circuit Breaker + Retry)**
   - CircuitBreaker applied first (outermost) to protect against cascades
   - Retry applied second (inner) to recover from transient errors
   - Bulkhead (can be added) as third layer for concurrency control
   - Layered approach allows each pattern to focus on specific concerns

6. **Observability endpoints**
   - Real-time metrics via `/actuator/*` endpoints
   - Custom endpoints for pattern-specific insights
   - No external monitoring infrastructure required
   - Enables testing pattern behavior without complex tooling

---

## 12. Conclusion

This project demonstrates how four complementary design patterns —**Circuit Breaker**, **Retry**, **Cache**, and **Strategy** — can be composed cleanly in a Spring Boot application using Resilience4j and Spring annotations.

The patterns are **complementary**:

- **Temporal Protection** (Circuit Breaker + Retry)
  - Circuit Breaker stops all calls when a service is failing
  - Retry recovers from transient failures automatically
  - Together they prevent cascading failures

- **Spatial Protection** (Cache + Strategy)
  - Cache reduces expensive database queries
  - Strategy allows runtime selection without coupling
  - Together they improve performance and flexibility

- **Production-Ready Implementation**
  - Declarative, annotation-based patterns
  - Zero framework coupling in business logic
  - Comprehensive observability and monitoring
  - Self-contained with mock external services

**Key takeaways:**

1. Design patterns make distributed systems robust by isolating failure domains
2. Resilience4j provides production-grade implementations without boilerplate
3. Spring Boot's declarative model keeps business logic clean
4. Layered pattern composition (Circuit Breaker → Retry → Cache) provides comprehensive protection
5. Observable patterns enable confidence in production deployments

This architecture ensures that a failing or slow external service (GPS tracking, payment gateway) cannot cause a full system outage, making the food delivery application significantly more resilient in production distributed environments.

---

## Repository & Source Code

Full source code available at:  
**https://github.com/Nourhene-z/smart-food-delivery-system**

---

**Prepared for:** Academic Submission / Professional Reference  
**Date:** May 2026  
**Status:** Production Ready
