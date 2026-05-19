# Smart Food Delivery System

A distributed food delivery backend system demonstrating advanced software design patterns and resilience mechanisms using Spring Boot, Resilience4j, and Redis.

## 🎯 Project Overview

The Smart Food Delivery System is a production-grade backend application that simulates a complete food delivery ecosystem. It demonstrates critical enterprise software patterns including:

- **Circuit Breaker Pattern** - Protecting calls to external GPS services
- **Retry Pattern** - Automatic retry of failed delivery tracking requests
- **Cache Pattern** - Redis-based caching for restaurant and menu data
- **Strategy Pattern** - Dynamic selection of payment processing methods

## 🏗️ System Architecture

The system consists of six core modules:

```
┌─────────────────────────────────────────────────────────────┐
│                    REST API Layer                            │
├─────────────────────────────────────────────────────────────┤
│  OrderController | RestaurantController | PaymentController │
│  DeliveryController | CustomerController | HealthController  │
├─────────────────────────────────────────────────────────────┤
│                    Service Layer                             │
├─────────────────────────────────────────────────────────────┤
│  OrderService | PaymentService | DeliveryService |          │
│  RestaurantService | CustomerService                        │
├─────────────────────────────────────────────────────────────┤
│                    Data Access Layer                         │
├─────────────────────────────────────────────────────────────┤
│  OrderRepository | PaymentRepository | DeliveryRepository   │
│  RestaurantRepository | CustomerRepository                   │
├─────────────────────────────────────────────────────────────┤
│         Database (MySQL) | Cache (Redis)                    │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Technology Stack

| Component       | Version | Purpose               |
| --------------- | ------- | --------------------- |
| Java            | 21      | Programming language  |
| Spring Boot     | 3.5.0   | Application framework |
| Spring Data JPA | Latest  | ORM layer             |
| MySQL           | 8.0     | Primary database      |
| Redis           | 7.0     | Caching layer         |
| Resilience4j    | 2.1.0   | Resilience patterns   |
| OpenFeign       | 4.1.0   | HTTP client           |
| Lombok          | Latest  | Code generation       |
| Maven           | 3.9+    | Build tool            |

## 🔧 Design Patterns Implemented

### 1. Circuit Breaker Pattern

**Location**: `DeliveryService.getDeliveryWithLocation()`

Protects the application from cascading failures when calling external GPS services:

```java
@CircuitBreaker(name = "gpsServiceCircuitBreaker", fallbackMethod = "getDeliveryFallback")
@Retry(name = "deliveryTrackingRetry")
public DeliveryDto getDeliveryWithLocation(Long deliveryId) {
    // GPS service call with protection
}
```

**States**:

- **CLOSED**: Normal operation, requests pass through
- **OPEN**: Service failure detected, requests fail fast
- **HALF_OPEN**: Recovery attempt, limited requests allowed

### 2. Retry Pattern

**Location**: `PaymentService`, `DeliveryService`

Automatically retries failed external requests with exponential backoff:

```yaml
resilience4j:
  retry:
    instances:
      deliveryTrackingRetry:
        maxAttempts: 3
        waitDuration: 2000
```

### 3. Cache Pattern

**Location**: `RestaurantService`

Uses Redis for caching expensive database queries:

```java
@Cacheable(value = "restaurants", unless = "#result.isEmpty()")
public List<RestaurantDto> getAllRestaurants() { ... }

@Cacheable(value = "topRatedRestaurants")
public List<RestaurantDto> getTopRatedRestaurants() { ... }
```

### 4. Strategy Pattern

**Location**: `PaymentService` with multiple `PaymentStrategy` implementations

Dynamically selects payment processing strategy at runtime:

```java
PaymentStrategy strategy = paymentStrategyFactory.getStrategy(paymentMethod);
boolean success = strategy.processPayment(orderId, amount);
```

**Supported Strategies**:

- `CreditCardPayment` - 95% success rate simulation
- `PayPalPayment` - 97% success rate simulation
- `CashPayment` - 100% success rate

## 📊 Database Design

### Entities

#### Customer

```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Restaurant

```sql
CREATE TABLE restaurants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    rating DOUBLE DEFAULT 4.0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Order

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    delivery_address VARCHAR(255) NOT NULL,
    special_instructions VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Delivery

```sql
CREATE TABLE deliveries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL UNIQUE,
    driver_name VARCHAR(100),
    driver_phone VARCHAR(20),
    vehicle_number VARCHAR(20),
    estimated_time_minutes INT,
    actual_time_minutes INT,
    delivery_status VARCHAR(50) NOT NULL,
    current_latitude DOUBLE,
    current_longitude DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 🔌 REST API Endpoints

### Orders

```
POST   /api/orders                          - Create new order
GET    /api/orders/{orderId}                - Get order details
GET    /api/orders/customer/{customerId}    - Get customer orders
PUT    /api/orders/{orderId}/status         - Update order status
DELETE /api/orders/{orderId}                - Cancel order
```

### Restaurants

```
GET    /api/restaurants                     - Get all restaurants (cached)
GET    /api/restaurants/top-rated           - Get top-rated restaurants (cached)
GET    /api/restaurants/category/{category} - Get restaurants by category
GET    /api/restaurants/{restaurantId}      - Get restaurant details
POST   /api/restaurants                     - Create new restaurant
```

### Payments

```
POST   /api/payments/process                - Process payment (Strategy Pattern)
POST   /api/payments/refund                 - Refund payment
```

### Delivery

```
POST   /api/delivery/{orderId}/assign       - Assign delivery to order
GET    /api/delivery/{deliveryId}           - Get delivery (Circuit Breaker + GPS)
GET    /api/delivery/order/{orderId}        - Get delivery by order
PUT    /api/delivery/{deliveryId}/status    - Update delivery status
GET    /api/delivery/active                 - Get active deliveries
```

### Customers

```
GET    /api/customers/{customerId}          - Get customer details
GET    /api/customers/email/{email}         - Get customer by email
GET    /api/customers                       - Get all customers
POST   /api/customers                       - Create new customer
PUT    /api/customers/{customerId}          - Update customer
DELETE /api/customers/{customerId}          - Delete customer
```

### System

```
GET    /api/health                          - Health check
GET    /api/info                            - Application info
```

## 🚀 Setup Instructions

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+
- Docker & Docker Compose (optional)

### Local Setup

#### 1. Install MySQL

```bash
# Windows (using chocolatey)
choco install mysql

# macOS (using brew)
brew install mysql

# Linux (Ubuntu/Debian)
sudo apt-get install mysql-server
```

#### 2. Create Database

```sql
CREATE DATABASE food_delivery_db;
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppass';
GRANT ALL PRIVILEGES ON food_delivery_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. Install Redis

```bash
# Windows (using WSL)
wsl
sudo apt-get install redis-server

# macOS
brew install redis

# Linux
sudo apt-get install redis-server
```

#### 4. Clone and Build

```bash
git clone https://github.com/yourusername/smart-food-delivery-system.git
cd smart-food-delivery-system
mvn clean install
```

#### 5. Run Application

```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

### Docker Setup

```bash
# Build and run with Docker Compose
docker-compose up -d

# Check services
docker-compose ps

# View logs
docker-compose logs -f app
```

## 📝 API Examples

### Create Customer

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Main St",
    "phone": "1234567890"
  }'
```

### Get Top-Rated Restaurants

```bash
curl -X GET http://localhost:8080/api/restaurants/top-rated
```

### Create Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "totalAmount": 50.0,
    "deliveryAddress": "123 Main St",
    "paymentMethod": "CREDIT_CARD"
  }'
```

### Process Payment

```bash
curl -X POST http://localhost:8080/api/payments/process \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 50.0,
    "paymentMethod": "CREDIT_CARD"
  }'
```

### Assign Delivery

```bash
curl -X POST http://localhost:8080/api/delivery/1/assign
```

### Get Delivery with Location (Circuit Breaker Test)

```bash
curl -X GET http://localhost:8080/api/delivery/1
```

## 🔍 Observability

The application exposes comprehensive metrics and health endpoints:

### Actuator Endpoints

```
GET /actuator                              - All endpoints
GET /actuator/health                       - Application health
GET /actuator/metrics                      - Metrics registry
GET /actuator/prometheus                   - Prometheus metrics
GET /actuator/circuitbreakers             - Circuit breaker status
GET /actuator/retries                     - Retry metrics
GET /actuator/caches                      - Cache statistics
```

### Prometheus Metrics

Access Prometheus metrics at: `http://localhost:8080/actuator/prometheus`

Key metrics:

- `resilience4j_circuitbreaker_*` - Circuit breaker metrics
- `resilience4j_retry_*` - Retry metrics
- `cache_size` - Cache statistics
- `http_server_requests_*` - HTTP request metrics

## 🧪 Testing

### Unit Tests

```bash
mvn test -Dtest=OrderServiceTest
mvn test -Dtest=PaymentServiceTest
```

### Integration Tests

```bash
mvn test -Dtest=FoodDeliveryIntegrationTest
```

### All Tests

```bash
mvn clean test
```

### Test Coverage

```bash
mvn clean test jacoco:report
```

## 📚 Key Design Decisions

1. **Layered Architecture**: Clear separation of concerns across controller, service, and repository layers
2. **DTO Pattern**: Data Transfer Objects for API communication to decouple entities from DTOs
3. **Service Layer Separation**: Business logic isolated in services for testability
4. **Global Exception Handling**: Centralized exception handling via GlobalExceptionHandler
5. **Circuit Breaker at Service Level**: Early detection and recovery from failures
6. **Redis Caching**: Reduced database load for read-heavy operations
7. **Strategy Factory**: Extensible payment method support without modifying existing code
8. **Comprehensive Logging**: Detailed logging for debugging and monitoring

## 🔐 Security Considerations

- Non-root user in Docker container
- Input validation with Jakarta Bean Validation
- SQL parameterization via JPA
- Exception information sanitization in API responses
- CORS configuration available for frontend integration
- Health endpoints protected in production deployments

## 📈 Performance Optimization

1. **Database Indexing**: Indexes on frequently queried columns
2. **Redis Caching**: 1-hour TTL for restaurant data
3. **Connection Pooling**: Optimized database connection pool
4. **Async Processing**: Notification service for non-critical operations
5. **Circuit Breaker Fallback**: Prevents cascading failures

## 🛠️ Troubleshooting

### MySQL Connection Issues

```bash
# Check MySQL is running
mysql -u root -p

# Verify database
mysql -u appuser -p food_delivery_db -e "SHOW TABLES;"
```

### Redis Connection Issues

```bash
# Check Redis is running
redis-cli ping
# Should return: PONG

# Test connection
redis-cli --raw
> PING
> quit
```

### Application won't start

```bash
# Check logs
tail -f logs/application.log

# Clear H2 database (dev only)
rm -f database.h2.db
```

## 📖 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Redis Documentation](https://redis.io/documentation)
- [Design Patterns](https://refactoring.guru/design-patterns)

## 👥 Team

Design Patterns Team - 2024

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please follow the existing code style and add tests for new features.

## 🐛 Known Limitations

1. Mock external services (GPS, Payment Gateway) are simulated for demonstration
2. Currently supports single-instance deployment; distributed tracing requires configuration
3. Authentication and authorization not implemented (can be added with Spring Security)
4. Rate limiting not implemented (can be added with Spring Cloud Gateway)

## 🚦 Status Codes

- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request parameters
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Maintenance Status**: Active
#   s m a r t - f o o d - d e l i v e r y - s y s t e m  
 