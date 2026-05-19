# Contributing to Smart Food Delivery System

Thank you for your interest in contributing to the Smart Food Delivery System project! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Respect intellectual property rights
- Report security issues responsibly

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+
- Git

### Fork & Clone

```bash
# Fork the repository on GitHub
git clone https://github.com/yourusername/smart-food-delivery-system.git
cd smart-food-delivery-system
git remote add upstream https://github.com/original/smart-food-delivery-system.git
```

### Setup Development Environment

```bash
# Create development branch
git checkout -b feature/your-feature-name

# Build project
make build

# Run tests
make test

# Start development environment
make dev-setup
```

## Development Guidelines

### Code Style

1. **Java Conventions**
   - Follow Google Java Style Guide
   - Use 4-space indentation
   - Keep lines under 100 characters
   - Use meaningful variable names

2. **Naming Conventions**
   - Classes: `PascalCase` (e.g., `OrderService`)
   - Methods: `camelCase` (e.g., `createOrder()`)
   - Constants: `UPPER_SNAKE_CASE` (e.g., `DEFAULT_TIMEOUT`)
   - Packages: lowercase with dots (e.g., `com.designpatterns.fooddelivery`)

3. **Annotations**
   - Use Lombok annotations to reduce boilerplate
   - Apply `@Slf4j` for logging
   - Use `@RequiredArgsConstructor` for dependency injection

4. **Documentation**
   - Add JavaDoc for public methods
   - Include parameter descriptions
   - Document exceptions thrown
   - Add usage examples for complex methods

```java
/**
 * Creates a new order for the customer.
 *
 * @param request the order creation request containing customer and restaurant IDs
 * @return the created order DTO
 * @throws ResourceNotFoundException if customer or restaurant not found
 * @throws IllegalArgumentException if validation fails
 */
public OrderDto createOrder(CreateOrderRequest request) {
    // Implementation
}
```

### Testing Requirements

1. **Unit Tests**
   - Test coverage minimum: 80%
   - Test file naming: `*Test.java`
   - Location: `src/test/java/`
   - Use `@ExtendWith(MockitoExtension.class)`

2. **Integration Tests**
   - Test complete workflows
   - File naming: `*IntegrationTest.java`
   - Use `@SpringBootTest` and `@AutoConfigureMockMvc`
   - Clean up database after tests

3. **Running Tests**
   ```bash
   make test                    # All unit tests
   make test-single TEST=OrderServiceTest
   make integration-test        # Integration tests
   make coverage               # Coverage report
   ```

### Design Patterns

Follow SOLID principles:

- **S**ingle Responsibility: One reason to change
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subtypes must be substitutable
- **I**nterface Segregation: Clients should not depend on methods they don't use
- **D**ependency Inversion: Depend on abstractions, not concrete classes

### Commit Guidelines

1. **Commit Messages**

   ```
   Type: Short description (50 chars max)

   Detailed explanation (wrap at 72 chars)
   - Explain what changed
   - Explain why it changed
   - Reference issues if applicable

   Fixes #123
   ```

2. **Commit Types**
   - `feat:` New feature
   - `fix:` Bug fix
   - `refactor:` Code restructuring
   - `docs:` Documentation
   - `test:` Tests
   - `chore:` Build, dependencies
   - `perf:` Performance improvements

3. **Example Commits**
   ```bash
   git commit -m "feat: Add circuit breaker for GPS service"
   git commit -m "fix: Handle null delivery status in response"
   git commit -m "docs: Update API documentation"
   ```

## Creating Pull Requests

### Before Submitting

```bash
# Update from upstream
git fetch upstream
git rebase upstream/main

# Run full test suite
make ci-build

# Format code
make format

# Push changes
git push origin feature/your-feature-name
```

### PR Description Template

```markdown
## Description

Brief description of changes

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues

Fixes #123

## Testing

- [ ] Unit tests added/updated
- [ ] Integration tests added
- [ ] Manual testing completed

## Checklist

- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests pass locally
- [ ] Dependencies updated (if changed)
```

## Architecture & Design

### Layered Architecture

```
Controller Layer (REST endpoints)
    ↓
Service Layer (Business logic)
    ↓
Repository Layer (Data access)
    ↓
Database (MySQL)
```

### Adding New Features

1. **Entity Layer**
   - Create JPA entity with proper annotations
   - Add validation annotations

2. **DTO Layer**
   - Create request/response DTOs
   - Add validation annotations

3. **Repository Layer**
   - Extend JpaRepository
   - Add custom query methods if needed

4. **Service Layer**
   - Implement business logic
   - Apply design patterns as needed
   - Add @Transactional where appropriate

5. **Controller Layer**
   - Create REST endpoints
   - Return ApiResponse wrapper
   - Document with JavaDoc

6. **Testing**
   - Unit tests for service
   - Integration tests for complete workflow

### Design Patterns Usage

- **Circuit Breaker**: Protect external service calls
- **Retry**: Automatically retry failed operations
- **Cache**: Store frequently accessed data
- **Strategy**: Implement pluggable algorithms
- **Factory**: Create objects based on conditions
- **DTO**: Decouple API contracts from entities

## Performance Considerations

1. **Database**
   - Use indexes for frequently queried columns
   - Implement pagination for large result sets
   - Use lazy loading for relationships

2. **Caching**
   - Cache expensive queries
   - Set appropriate TTL values
   - Invalidate cache on updates

3. **Async Operations**
   - Use async methods for I/O operations
   - Don't block request threads
   - Implement proper error handling

## Security Guidelines

1. **Input Validation**
   - Validate all input parameters
   - Use Jakarta Bean Validation annotations
   - Sanitize user input

2. **Error Handling**
   - Don't expose sensitive information in errors
   - Log security events
   - Use appropriate HTTP status codes

3. **Dependencies**
   - Keep dependencies updated
   - Use `make deps-outdated` to check updates
   - Review security advisories

## Documentation

### Updating Documentation

1. **Code Documentation**
   - Add/update JavaDoc for public classes/methods
   - Include usage examples
   - Document exceptions

2. **Project Documentation**
   - Update README.md for user-facing changes
   - Update REPORT.md for architectural changes
   - Update CHANGELOG.md with version details

3. **API Documentation**
   - Include example requests/responses
   - Document path parameters
   - Document query parameters

## Feedback & Support

### Getting Help

1. Check existing documentation
2. Search closed issues
3. Ask in discussions section
4. Create new issue if needed

### Issue Template

```markdown
**Describe the issue:**
Clear description of what's happening

**Steps to reproduce:**

1. Step one
2. Step two

**Expected behavior:**
What should happen

**Actual behavior:**
What actually happens

**Environment:**

- Java version: 21
- Spring Boot version: 3.5.0
- OS: Windows/Linux/macOS

**Screenshots:**
If applicable
```

## Release Process

1. Update version in `pom.xml`
2. Update `CHANGELOG.md`
3. Create release tag: `git tag v1.0.0`
4. Push tag: `git push origin v1.0.0`
5. Create GitHub release with CHANGELOG

## Code Review Process

- At least 2 approvals required
- All tests must pass
- No merge conflicts
- Code style compliance verified
- Documentation complete

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to make this project better! 🚀
