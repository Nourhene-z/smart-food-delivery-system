# Smart Food Delivery System - Complete Documentation Index

## 📋 Project Overview

This is a **production-quality Spring Boot application** demonstrating advanced software design patterns and resilience mechanisms for a food delivery system.

**Quick Links:**

- [README.md](README.md) - Complete project documentation and API guide
- [REPORT.md](REPORT.md) - Academic technical report with architecture
- [SETUP.md](SETUP.md) - Quick start and installation guide
- [CHANGELOG.md](CHANGELOG.md) - Version history and features
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contributing guidelines

---

## 🏗️ Architecture & Design

### Core Modules

1. **Order Management** - Create, track, and manage food orders
2. **Restaurant Service** - Browse restaurants with caching
3. **Delivery Service** - Assign and track deliveries (Circuit Breaker)
4. **Payment Service** - Process payments (Strategy Pattern)
5. **Customer Service** - Manage customer profiles
6. **Notification Service** - Send order/delivery updates

### Design Patterns Implemented

| Pattern             | Location                                    | Purpose                                     |
| ------------------- | ------------------------------------------- | ------------------------------------------- |
| **Circuit Breaker** | `DeliveryService.java`                      | Protect GPS service from cascading failures |
| **Retry**           | `DeliveryService.java`                      | Automatic retry with exponential backoff    |
| **Cache**           | `RestaurantService.java`                    | Redis caching for restaurant data           |
| **Strategy**        | `service/PaymentService.java` + `strategy/` | Dynamic payment method selection            |
| **Factory**         | `PaymentStrategyFactory.java`               | Create strategy instances                   |
| **DTO**             | `dto/` package                              | Decouple API from entities                  |
| **Repository**      | `repository/` package                       | Data access abstraction                     |

---

## 🛠️ Technology Stack

| Component        | Version | Purpose             |
| ---------------- | ------- | ------------------- |
| **Java**         | 21 LTS  | Runtime environment |
| **Spring Boot**  | 3.5.0   | Framework           |
| **MySQL**        | 8.0     | Primary database    |
| **Redis**        | 7.0     | Cache layer         |
| **Resilience4j** | 2.1.0   | Fault tolerance     |
| **OpenFeign**    | 4.1.0   | HTTP client         |
| **Lombok**       | Latest  | Code generation     |
| **JUnit 5**      | Latest  | Testing framework   |

---

## 📁 Project Structure

```
smart-food-delivery-system/
├── src/main/java/.../
│   ├── config/                 # Spring configuration
│   ├── controller/             # REST endpoints (6 controllers)
│   ├── service/                # Business logic (5 services)
│   ├── repository/             # Data access (4 repositories)
│   ├── entity/                 # JPA entities (4 entities)
│   ├── dto/                    # Data transfer objects (8 DTOs)
│   ├── strategy/               # Strategy pattern (4 classes)
│   ├── client/                 # HTTP clients (Feign)
│   ├── mock/                   # Mock services (3 services)
│   └── exception/              # Error handling (3 classes)
├── src/test/java/.../
│   ├── service/                # Unit tests (2 tests)
│   └── integration/            # Integration tests (1 test)
├── Dockerfile                  # Container image
├── docker-compose.yml          # Multi-container setup
├── pom.xml                     # Maven configuration
├── README.md                   # Main documentation
├── REPORT.md                   # Technical report
├── SETUP.md                    # Setup instructions
├── PROJECT_STRUCTURE.md        # This file structure
├── CONTRIBUTING.md             # Contribution guidelines
├── CHANGELOG.md                # Version history
├── Makefile                    # Build automation
├── postman_collection.json     # API testing collection
├── .env.example                # Environment variables template
└── .gitignore                  # Git ignore rules
```

---

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+

### Installation (5 minutes)

```bash
# 1. Clone repository
git clone <repo-url>
cd smart-food-delivery-system

# 2. Setup databases
mysql -u root -p < schema.sql

# 3. Build project
mvn clean install

# 4. Run application
mvn spring-boot:run

# 5. Test health
curl http://localhost:8080/api/health
```

### Using Docker (Recommended)

```bash
docker-compose up -d
# All services start: MySQL, Redis, Application
# Access at http://localhost:8080
```

See [SETUP.md](SETUP.md) for detailed instructions.

---

## 📚 Documentation Files

### User Documentation

| File                                               | Purpose                          | Audience       |
| -------------------------------------------------- | -------------------------------- | -------------- |
| [README.md](README.md)                             | Complete guide with API examples | All users      |
| [SETUP.md](SETUP.md)                               | Installation and configuration   | New developers |
| [postman_collection.json](postman_collection.json) | API testing collection           | API users      |

### Developer Documentation

| File                                         | Purpose                 | Audience     |
| -------------------------------------------- | ----------------------- | ------------ |
| [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) | Code organization       | Developers   |
| [CONTRIBUTING.md](CONTRIBUTING.md)           | Contribution guidelines | Contributors |
| [Makefile](Makefile)                         | Build commands          | Developers   |

### Project Documentation

| File                         | Purpose                      | Audience          |
| ---------------------------- | ---------------------------- | ----------------- |
| [REPORT.md](REPORT.md)       | Academic technical report    | Architects, Leads |
| [CHANGELOG.md](CHANGELOG.md) | Version history and features | All stakeholders  |

---

## 💻 Common Commands

### Build & Test

```bash
make build              # Build project
make test              # Run unit tests
make integration-test  # Run integration tests
make coverage         # Generate coverage report
```

### Running

```bash
make run              # Start application
make docker-up        # Start Docker services
make docker-down      # Stop Docker services
```

### Database

```bash
make db-create        # Create database
make db-reset         # Reset database
```

### Monitoring

```bash
make health           # Check health
make metrics          # View metrics
make circuitbreaker   # Circuit breaker status
make caches          # Cache statistics
```

See [Makefile](Makefile) for all available commands.

---

## 🔗 API Endpoints

### Customer Management

- `POST /api/customers` - Create customer
- `GET /api/customers/{id}` - Get customer
- `GET /api/customers` - List all customers

### Restaurants (Cached)

- `GET /api/restaurants` - All restaurants (cached)
- `GET /api/restaurants/top-rated` - Top rated (cached)
- `GET /api/restaurants/category/{category}` - By category (cached)

### Orders

- `POST /api/orders` - Create order
- `GET /api/orders/{id}` - Get order
- `GET /api/orders/customer/{customerId}` - Customer orders
- `PUT /api/orders/{id}/status` - Update status
- `DELETE /api/orders/{id}` - Cancel order

### Payments (Strategy Pattern)

- `POST /api/payments/process` - Process payment (Credit Card, PayPal, or Cash)
- `POST /api/payments/refund` - Refund payment

### Delivery (Circuit Breaker)

- `POST /api/delivery/{orderId}/assign` - Assign delivery
- `GET /api/delivery/{id}` - Get delivery (Circuit Breaker protected)
- `GET /api/delivery/order/{orderId}` - Get delivery by order
- `PUT /api/delivery/{id}/status` - Update status
- `GET /api/delivery/active` - Active deliveries

### Monitoring

- `GET /api/health` - Health check
- `GET /api/info` - Application info
- `GET /actuator/circuitbreakers` - Circuit breaker metrics
- `GET /actuator/retries` - Retry metrics
- `GET /actuator/caches` - Cache statistics

See [README.md](README.md#-api-examples) for detailed examples.

---

## 🧪 Testing

### Unit Tests

- Located in `src/test/java/com/designpatterns/fooddelivery/service/`
- Files: `OrderServiceTest.java`, `PaymentServiceTest.java`
- Uses Mockito for mocking dependencies

### Integration Tests

- Located in `src/test/java/com/designpatterns/fooddelivery/integration/`
- File: `FoodDeliveryIntegrationTest.java`
- Tests complete workflows with MockMvc
- Uses H2 in-memory database for test isolation

### Running Tests

```bash
make test                      # All unit tests
make test-single TEST=OrderServiceTest
make integration-test          # Integration tests
make coverage                 # Coverage report (target/site/jacoco/index.html)
```

---

## 🐳 Docker Deployment

### Build Image

```bash
docker build -t smart-food-delivery:1.0.0 .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

### Services Started

1. **MySQL** (port 3306)
2. **Redis** (port 6379)
3. **Application** (port 8080)

All services have health checks and auto-restart.

---

## 📊 Monitoring & Observability

### Health Endpoints

- `/api/health` - Application health
- `/actuator/health` - Detailed health
- `/api/info` - Application info

### Metrics

- `/actuator/metrics` - All metrics
- `/actuator/prometheus` - Prometheus format
- `/actuator/circuitbreakers` - Circuit breaker metrics
- `/actuator/retries` - Retry metrics
- `/actuator/caches` - Cache statistics

### Logging

- Log file: `logs/application.log`
- Error file: `logs/error.log`
- Configuration: `src/main/resources/logback-spring.xml`
- Levels can be configured in `application.yml`

---

## 🔐 Security Considerations

1. **Input Validation** - All inputs validated with Jakarta Bean Validation
2. **Error Handling** - Sensitive information not exposed in errors
3. **SQL Injection Prevention** - JPA/Hibernate parameterized queries
4. **Container Security** - Non-root user in Docker
5. **Secrets** - Use environment variables for sensitive data

See [REPORT.md](REPORT.md) for security considerations section.

---

## 🚦 Design Pattern Demonstrations

### 1. Circuit Breaker (GPS Service)

- **Location**: `DeliveryService.getDeliveryWithLocation()`
- **Purpose**: Prevent cascading failures from GPS service
- **States**: CLOSED → OPEN → HALF_OPEN → CLOSED
- **Configuration**: 100 calls window, 50% failure threshold

### 2. Retry (Delivery Tracking)

- **Location**: `DeliveryService.getDeliveryWithLocation()`
- **Purpose**: Automatically retry failed requests
- **Configuration**: 3 attempts, 2-second backoff

### 3. Cache (Restaurant Data)

- **Location**: `RestaurantService` methods
- **Purpose**: Reduce database load for frequently accessed data
- **Backend**: Redis with 1-hour TTL

### 4. Strategy (Payment Processing)

- **Location**: `PaymentService.processPayment()`
- **Strategies**: Credit Card (95% success), PayPal (97%), Cash (100%)
- **Purpose**: Dynamic payment method selection at runtime

---

## 📖 API Testing

### Using Postman

1. Import `postman_collection.json` into Postman
2. Set variable `{{baseUrl}}` to `http://localhost:8080`
3. Run collection requests

### Using cURL

```bash
# Create customer
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com",...}'

# Process payment
curl -X POST http://localhost:8080/api/payments/process \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"amount":50.0,"paymentMethod":"CREDIT_CARD"}'
```

---

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for:

- Coding standards
- Testing requirements
- Commit message guidelines
- Pull request process

---

## 📝 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file.

---

## 🔗 Related Resources

- [Spring Boot Docs](https://docs.spring.io/spring-boot/)
- [Resilience4j Docs](https://resilience4j.readme.io/)
- [Redis Documentation](https://redis.io/documentation)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

## 📞 Support

For issues or questions:

1. Check existing documentation
2. Review GitHub issues
3. Check SETUP.md troubleshooting section
4. Create new GitHub issue with details

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready
