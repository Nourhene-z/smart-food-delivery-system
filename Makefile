.PHONY: help build test run clean docker-up docker-down

# Variables
JAVA := java
MVN := mvn
DOCKER := docker
DOCKER_COMPOSE := docker-compose
APP_NAME := smart-food-delivery-system
APP_VERSION := 1.0.0
PORT := 8080

help:
	@echo "$(APP_NAME) - Build and Development Commands"
	@echo ""
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@echo "  build              Build the project"
	@echo "  test               Run unit tests"
	@echo "  integration-test   Run integration tests"
	@echo "  run                Run the application"
	@echo "  clean              Clean build artifacts"
	@echo "  package            Package as JAR"
	@echo "  docker-build       Build Docker image"
	@echo "  docker-up          Start Docker services"
	@echo "  docker-down        Stop Docker services"
	@echo "  logs               View application logs"
	@echo "  health             Check application health"
	@echo "  coverage           Generate test coverage report"
	@echo "  lint               Run code analysis"

# Build and Compile
build:
	@echo "Building $(APP_NAME)..."
	$(MVN) clean install -DskipTests

build-quick:
	@echo "Quick build (no tests)..."
	$(MVN) clean package -DskipTests

# Testing
test:
	@echo "Running unit tests..."
	$(MVN) test

test-single:
	@echo "Running single test (provide TEST=ClassName)"
	$(MVN) test -Dtest=$(TEST)

integration-test:
	@echo "Running integration tests..."
	$(MVN) test -Dtest=*IntegrationTest

coverage:
	@echo "Generating test coverage report..."
	$(MVN) clean test jacoco:report
	@echo "Coverage report generated in target/site/jacoco/index.html"

# Packaging
package:
	@echo "Packaging as JAR..."
	$(MVN) clean package -DskipTests

# Running Application
run:
	@echo "Starting $(APP_NAME) on port $(PORT)..."
	$(MVN) spring-boot:run

run-debug:
	@echo "Starting in debug mode..."
	$(MVN) spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Cleaning
clean:
	@echo "Cleaning build artifacts..."
	$(MVN) clean
	@rm -rf logs/
	@rm -rf .m2/

# Docker Commands
docker-build:
	@echo "Building Docker image..."
	$(DOCKER) build -t $(APP_NAME):$(APP_VERSION) .
	$(DOCKER) tag $(APP_NAME):$(APP_VERSION) $(APP_NAME):latest

docker-up:
	@echo "Starting Docker services..."
	$(DOCKER_COMPOSE) up -d
	@echo "Services started. Application available at http://localhost:$(PORT)"

docker-down:
	@echo "Stopping Docker services..."
	$(DOCKER_COMPOSE) down

docker-logs:
	@echo "Showing Docker logs..."
	$(DOCKER_COMPOSE) logs -f app

docker-clean:
	@echo "Removing Docker containers and volumes..."
	$(DOCKER_COMPOSE) down -v
	$(DOCKER) rmi $(APP_NAME):$(APP_VERSION) $(APP_NAME):latest

# Health and Monitoring
health:
	@echo "Checking application health..."
	@curl -s http://localhost:$(PORT)/api/health | jq . || echo "Application not responding"

info:
	@echo "Getting application info..."
	@curl -s http://localhost:$(PORT)/api/info | jq .

metrics:
	@echo "Getting metrics..."
	@curl -s http://localhost:$(PORT)/actuator/metrics | jq .

circuitbreaker:
	@echo "Circuit Breaker Status:"
	@curl -s http://localhost:$(PORT)/actuator/circuitbreakers | jq .

retries:
	@echo "Retry Metrics:"
	@curl -s http://localhost:$(PORT)/actuator/retries | jq .

caches:
	@echo "Cache Status:"
	@curl -s http://localhost:$(PORT)/actuator/caches | jq .

logs:
	@echo "Showing application logs..."
	@tail -f logs/application.log

# Database
db-create:
	@echo "Creating database..."
	@mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS food_delivery_db; \
	CREATE USER IF NOT EXISTS 'appuser'@'localhost' IDENTIFIED BY 'apppass'; \
	GRANT ALL PRIVILEGES ON food_delivery_db.* TO 'appuser'@'localhost'; \
	FLUSH PRIVILEGES;"

db-drop:
	@echo "Dropping database..."
	@mysql -u root -p -e "DROP DATABASE IF EXISTS food_delivery_db;"

db-reset: db-drop db-create
	@echo "Database reset complete"

# Code Quality
lint:
	@echo "Running code analysis..."
	$(MVN) verify

format:
	@echo "Formatting code..."
	$(MVN) java-formatter:format

# Documentation
docs:
	@echo "Generating documentation..."
	@echo "README.md - Project overview"
	@echo "REPORT.md - Technical report"
	@echo "SETUP.md - Setup instructions"
	@echo "PROJECT_STRUCTURE.md - Directory structure"

# Development Workflow
dev-setup: clean build db-reset
	@echo "Development environment ready!"

dev-start: docker-up run
	@echo "Development environment started"

dev-stop: docker-down
	@echo "Development environment stopped"

# Utilities
version:
	@echo "$(APP_NAME) version $(APP_VERSION)"
	@$(JAVA) -version

deps:
	@echo "Showing dependencies..."
	$(MVN) dependency:tree

deps-outdated:
	@echo "Checking for outdated dependencies..."
	$(MVN) versions:display-dependency-updates

update-deps:
	@echo "Updating dependencies..."
	$(MVN) versions:use-latest-releases

# CI/CD
ci-build: clean build test
	@echo "CI build completed"

ci-docker: package docker-build
	@echo "Docker image built for CI"

# Help for specific commands
help-docker:
	@echo "Docker Commands:"
	@echo "  make docker-build    - Build Docker image"
	@echo "  make docker-up        - Start services"
	@echo "  make docker-down      - Stop services"
	@echo "  make docker-logs      - View logs"

help-test:
	@echo "Testing Commands:"
	@echo "  make test             - Run all unit tests"
	@echo "  make test-single      - Run single test (TEST=ClassName)"
	@echo "  make integration-test - Run integration tests"
	@echo "  make coverage         - Generate coverage report"

help-db:
	@echo "Database Commands:"
	@echo "  make db-create       - Create database"
	@echo "  make db-drop         - Drop database"
	@echo "  make db-reset        - Reset database"

help-dev:
	@echo "Development Workflow:"
	@echo "  make dev-setup       - Setup development environment"
	@echo "  make dev-start       - Start with Docker"
	@echo "  make dev-stop        - Stop services"

.DEFAULT_GOAL := help
