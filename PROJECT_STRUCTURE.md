# Project Structure

```
smart-food-delivery-system/
├── src/
│   ├── main/
│   │   ├── java/com/designpatterns/fooddelivery/
│   │   │   ├── SmartFoodDeliveryApplication.java    # Main entry point
│   │   │   ├── client/
│   │   │   │   └── GpsServiceClient.java            # Feign client for GPS
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java                   # Application configuration
│   │   │   │   └── HttpClientConfig.java            # HTTP client setup
│   │   │   ├── controller/
│   │   │   │   ├── OrderController.java             # Order endpoints
│   │   │   │   ├── RestaurantController.java        # Restaurant endpoints
│   │   │   │   ├── PaymentController.java           # Payment endpoints
│   │   │   │   ├── DeliveryController.java          # Delivery endpoints
│   │   │   │   ├── CustomerController.java          # Customer endpoints
│   │   │   │   └── HealthController.java            # Health check endpoints
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java                 # Generic API response
│   │   │   │   ├── CustomerDto.java                 # Customer DTO
│   │   │   │   ├── RestaurantDto.java               # Restaurant DTO
│   │   │   │   ├── OrderDto.java                    # Order DTO
│   │   │   │   ├── DeliveryDto.java                 # Delivery DTO
│   │   │   │   ├── CreateOrderRequest.java          # Order creation request
│   │   │   │   ├── PaymentRequest.java              # Payment request
│   │   │   │   └── PaymentResponse.java             # Payment response
│   │   │   ├── entity/
│   │   │   │   ├── Customer.java                    # Customer entity
│   │   │   │   ├── Restaurant.java                  # Restaurant entity
│   │   │   │   ├── Order.java                       # Order entity
│   │   │   │   └── Delivery.java                    # Delivery entity
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java   # Not found exception
│   │   │   │   ├── PaymentException.java            # Payment exception
│   │   │   │   └── GlobalExceptionHandler.java      # Global error handler
│   │   │   ├── mock/
│   │   │   │   ├── MockGpsService.java              # Mock GPS service
│   │   │   │   ├── MockPaymentGateway.java          # Mock payment gateway
│   │   │   │   └── MockNotificationService.java     # Mock notification service
│   │   │   ├── repository/
│   │   │   │   ├── CustomerRepository.java          # Customer repository
│   │   │   │   ├── RestaurantRepository.java        # Restaurant repository
│   │   │   │   ├── OrderRepository.java             # Order repository
│   │   │   │   └── DeliveryRepository.java          # Delivery repository
│   │   │   ├── service/
│   │   │   │   ├── CustomerService.java             # Customer business logic
│   │   │   │   ├── RestaurantService.java           # Restaurant with caching
│   │   │   │   ├── OrderService.java                # Order processing
│   │   │   │   ├── PaymentService.java              # Payment with strategy
│   │   │   │   └── DeliveryService.java             # Delivery with circuit breaker
│   │   │   └── strategy/
│   │   │       ├── PaymentStrategy.java             # Strategy interface
│   │   │       ├── CreditCardPayment.java           # Credit card strategy
│   │   │       ├── PayPalPayment.java               # PayPal strategy
│   │   │       ├── CashPayment.java                 # Cash strategy
│   │   │       └── PaymentStrategyFactory.java      # Strategy factory
│   │   └── resources/
│   │       ├── application.yml                      # Main configuration
│   │       ├── application-dev.yml                  # Development profile
│   │       ├── application-test.yml                 # Test profile (in test/)
│   │       └── logback-spring.xml                   # Logging configuration
│   └── test/
│       ├── java/com/designpatterns/fooddelivery/
│       │   ├── service/
│       │   │   ├── OrderServiceTest.java            # Order service tests
│       │   │   └── PaymentServiceTest.java          # Payment service tests
│       │   └── integration/
│       │       └── FoodDeliveryIntegrationTest.java # Integration tests
│       └── resources/
│           └── application-test.yml                 # Test configuration
├── Dockerfile                                        # Docker image definition
├── docker-compose.yml                               # Docker compose for dev env
├── pom.xml                                          # Maven configuration
├── README.md                                        # Project documentation
├── REPORT.md                                        # Technical report
├── SETUP.md                                         # Setup guide
├── CHANGELOG.md                                     # Version history
├── .gitignore                                       # Git ignore file
├── Makefile                                         # Build automation
└── .github/
    └── copilot-instructions.md                      # GitHub Copilot config
```

## Key Directories

### src/main/java

- **client**: External service integrations (Feign clients)
- **config**: Spring configuration classes
- **controller**: REST API endpoints
- **dto**: Data Transfer Objects for API
- **entity**: JPA entities mapping to database tables
- **exception**: Custom exceptions and global handlers
- **mock**: Mock external services for testing
- **repository**: Data access layer
- **service**: Business logic layer
- **strategy**: Strategy pattern implementations

### src/main/resources

- **application.yml**: Main Spring Boot configuration
- **application-dev.yml**: Development-specific settings
- **logback-spring.xml**: Logging configuration

### src/test

- **service**: Unit tests for services
- **integration**: Integration tests for complete workflows

## Design Patterns Locations

1. **Circuit Breaker**: `service/DeliveryService.java`
2. **Retry**: `service/DeliveryService.java` and `service/PaymentService.java`
3. **Cache**: `service/RestaurantService.java`
4. **Strategy**: `strategy/` package and `service/PaymentService.java`

## Entity Relationships

```
Customer (1) ──────── (M) Order ────────── (1) Delivery
   │                    │
   └─ Email             ├─ Status (ENUM)
   └─ Address           └─ PaymentMethod

Restaurant (1) ──────── (M) Order
   │
   ├─ Rating
   ├─ Category
   └─ IsActive
```

## Configuration Files

| File                   | Purpose                             |
| ---------------------- | ----------------------------------- |
| `pom.xml`              | Maven dependencies and build config |
| `application.yml`      | Production configuration            |
| `application-dev.yml`  | Development overrides               |
| `application-test.yml` | Test configuration                  |
| `logback-spring.xml`   | Logging levels and appenders        |
| `Dockerfile`           | Container image definition          |
| `docker-compose.yml`   | Multi-container environment         |

## Module Dependencies

```
controller → service → repository → database
      ↓        ↓          ↓
    dto      strategy   entity
      ↓        ↓
exception  mock
```

## Dependency Injection

Key beans managed by Spring:

1. **Repositories**: Auto-generated from JpaRepository
2. **Services**: Singleton beans with transactional support
3. **Controllers**: Request-scoped beans
4. **Strategies**: Component-scoped payment strategies
5. **Factories**: Singleton beans for object creation
6. **Configuration**: Application configuration beans

---

**Last Updated**: December 2024
