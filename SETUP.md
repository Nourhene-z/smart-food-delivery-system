# Quick Start Guide

## Prerequisites

- Java 21+ ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- Maven 3.9+ ([Download](https://maven.apache.org/download.cgi))
- MySQL 8.0+ ([Download](https://dev.mysql.com/downloads/mysql/))
- Redis 7.0+ ([Download](https://redis.io/download))
- Git ([Download](https://git-scm.com/))

## Installation Steps

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/smart-food-delivery-system.git
cd smart-food-delivery-system
```

### 2. Database Setup

**Windows (Command Prompt):**

```bash
# Login to MySQL
mysql -u root -p

# Create database and user
CREATE DATABASE food_delivery_db;
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppass';
GRANT ALL PRIVILEGES ON food_delivery_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**Linux/macOS (Terminal):**

```bash
# Login to MySQL
mysql -u root -p

# Execute SQL commands as above
```

### 3. Redis Setup

**Windows (WSL):**

```bash
wsl
sudo apt-get update
sudo apt-get install redis-server
redis-server
```

**macOS:**

```bash
brew install redis
redis-server
```

**Linux:**

```bash
sudo apt-get update
sudo apt-get install redis-server
redis-server
```

### 4. Build Project

```bash
# Download dependencies
mvn clean install

# Build without tests (faster)
mvn clean package -DskipTests
```

### 5. Run Application

```bash
# Using Maven
mvn spring-boot:run

# Or run JAR directly
java -jar target/smart-food-delivery-system-1.0.0.jar
```

The application will be available at: `http://localhost:8080`

## Docker Quick Start

### With Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Clean up volumes
docker-compose down -v
```

### Manual Docker Build

```bash
# Build image
docker build -t smart-food-delivery:latest .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/food_delivery_db \
  -e SPRING_DATASOURCE_USERNAME=appuser \
  -e SPRING_DATASOURCE_PASSWORD=apppass \
  -e SPRING_REDIS_HOST=host.docker.internal \
  smart-food-delivery:latest
```

## Verification

### Health Check

```bash
curl http://localhost:8080/api/health
```

Expected response:

```json
{
  "status": "UP",
  "service": "Smart Food Delivery System",
  "version": "1.0.0"
}
```

### Create Test Customer

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "address": "123 Main St",
    "phone": "1234567890"
  }'
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=OrderServiceTest

# Generate test report
mvn test jacoco:report
```

## Common Issues & Solutions

### MySQL Connection Error

```
Error: Communications link failure
```

**Solution:**

```bash
# Check MySQL is running
mysql -u root -p
# If not running, restart MySQL
net start MySQL80  # Windows
brew services start mysql  # macOS
sudo systemctl start mysql  # Linux
```

### Redis Connection Error

```
Error: RedisConnectionException
```

**Solution:**

```bash
# Check Redis is running
redis-cli ping
# Should return: PONG

# If not running, start Redis
redis-server  # macOS/Linux
redis-server.exe  # Windows
```

### Port Already in Use

```
Error: Bind exception on port 8080
```

**Solution:**

```bash
# Change port in application.yml
server:
  port: 8081

# Or kill process using port
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -i :8080
kill -9 <PID>
```

## IDE Setup

### IntelliJ IDEA

1. Open Project: `File > Open > select project root`
2. Maven: Should auto-detect pom.xml
3. Mark Directories:
   - `src/main/java` as Sources
   - `src/test/java` as Tests
4. Run Configuration:
   - Create Run Configuration for main class
   - Add VM options: `-Dspring.profiles.active=dev`

### VS Code

1. Install Extensions:
   - Extension Pack for Java
   - REST Client
   - MySQL

2. Create launch.json:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot",
      "request": "launch",
      "mainClass": "com.designpatterns.fooddelivery.SmartFoodDeliveryApplication"
    }
  ]
}
```

## Next Steps

1. Read [README.md](README.md) for full documentation
2. Explore [REPORT.md](REPORT.md) for architecture details
3. Check [API Examples](README.md#-api-examples)
4. Review test files for usage patterns

## Support

For issues or questions:

1. Check troubleshooting section in README.md
2. Review logs: `logs/application.log`
3. Open GitHub issue with:
   - Error message
   - Steps to reproduce
   - Java/MySQL/Redis versions
   - Operating system

## Additional Resources

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/)
- [Resilience4j Docs](https://resilience4j.readme.io/)
- [Redis Docs](https://redis.io/documentation)
- [MySQL Docs](https://dev.mysql.com/doc/)
