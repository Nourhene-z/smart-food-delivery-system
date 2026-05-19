# Multi-stage Dockerfile for Smart Food Delivery System

# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="Design Patterns Team"
LABEL description="Smart Food Delivery System - Demonstrates resilience and design patterns"

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/target/smart-food-delivery-system-1.0.0.jar app.jar

# Create non-root user for security
RUN useradd -m -u 1000 appuser && \
    chown -R appuser:appuser /app

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD java -cp /app/app.jar org.springframework.boot.loader.JarLauncher \
        org.springframework.boot.loader.thin.JsonWriter /dev/null || exit 1

# Application port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
