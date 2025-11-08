# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot REST API for managing agricultural products (ProductoAgricola) and their harvest details (Cosecha). This is an academic project for Universidad de Ibagué that implements a master-detail relationship pattern with in-memory persistence using ConcurrentHashMap.

**Key Technologies:**
- Java 21
- Spring Boot 3.2.0
- Maven
- MySQL Database with JPA/Hibernate
- Jackson for JSON serialization
- Bean Validation (Jakarta)
- Properties-based configuration (centralized)

## Build and Run Commands

### Development
```bash
# Run application in development mode
mvn spring-boot:run

# Run with dev profile explicitly
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Clean and compile
mvn clean compile
```

### Testing
```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=ClassName

# Run tests with coverage
mvn test jacoco:report
```

### Build and Package
```bash
# Create executable JAR
mvn clean package

# Run the packaged JAR
java -jar target/agropecuario-rest-server-1.0.0.jar

# Build with production profile
mvn clean package -Pprod
```

### Application Access
- Server runs on: `http://localhost:8081` (configurable in `application.properties`)
- Health check: `http://localhost:8081/actuator/health`
- Statistics: `http://localhost:8081/api/productos/estadisticas`

### Configuration
All application settings are centralized in `application.properties` files:
- **`application.properties`** - Base configuration shared across all environments
- **`application-dev.properties`** - Development-specific overrides
- **`application-prod.properties`** - Production-specific overrides

**Quick configuration changes:**
- Change server port: Edit `server.port` in `application.properties`
- Change database connection: Edit `spring.datasource.*` properties
- Change CORS settings: Edit `cors.*` properties
- Change logging levels: Edit `logging.level.*` properties

See `CONFIGURATION.md` for complete configuration guide.

## Architecture

### Layered Architecture Pattern

The codebase follows a strict 4-layer architecture:

1. **Controller Layer** (`controller/`)
   - REST endpoints exposed via `@RestController`
   - Returns standardized `ApiResponseDTO<T>` for all responses
   - Uses `@Valid` for automatic validation
   - Important: `ProductoAgricolaController` handles BOTH producto endpoints AND nested cosecha endpoints

2. **Service Layer** (`service/` + `service/impl/`)
   - Business logic and validation
   - Interface-based design pattern
   - `ProductoAgricolaService` and `CosechaService` work independently

3. **Repository Layer** (`repository/` + `repository/impl/`)
   - Data access with in-memory storage
   - Uses `ConcurrentHashMap` for thread-safe operations
   - Auto-generates IDs: `AGR001`, `AGR002`, etc. for products; `COS001`, `COS002` for harvests
   - Pre-loaded with 3 test products on initialization

4. **Model/DTO Layer** (`model/`, `dto/`)
   - Domain entities with Bean Validation annotations
   - DTOs for request/response standardization

### Master-Detail Relationship

**CRITICAL ARCHITECTURAL PATTERN:**

The application implements a master-detail relationship:
- **Master:** `ProductoAgricola` (Agricultural Product)
- **Detail:** `Cosecha` (Harvest) - contains `productoId` foreign key

**Two ways to access Cosechas:**

1. **Nested routes** (preferred for master-detail operations):
   - `GET /api/productos/{productoId}/cosechas` - Get all harvests for a product
   - `POST /api/productos/{productoId}/cosechas` - Create harvest for a product
   - `GET /api/productos/{productoId}/cosechas/{cosechaId}` - Get specific harvest
   - `PUT /api/productos/{productoId}/cosechas/{cosechaId}` - Update harvest
   - `DELETE /api/productos/{productoId}/cosechas/{cosechaId}` - Delete harvest
   - `GET /api/productos/{productoId}/cosechas/estadisticas` - Harvest statistics for product

   **These endpoints are in `ProductoAgricolaController`** (not `CosechaController`)!

2. **Direct routes** (for general harvest operations):
   - `GET /api/cosechas` - List all harvests
   - `GET /api/cosechas/{id}` - Get harvest by ID
   - `POST /api/cosechas` - Create harvest
   - `PUT /api/cosechas/{id}` - Update harvest
   - `DELETE /api/cosechas/{id}` - Delete harvest

   These are in `CosechaController`.

### Response Standardization

All API responses use `ApiResponseDTO<T>`:
```java
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2025-10-20T10:30:00"
}
```

Error responses use `ErrorResponseDTO` via `GlobalExceptionHandler`.

The `ResponseBuilder` utility class provides convenient methods for building standardized responses.

### ID Generation Strategy

- Products: `AGR001`, `AGR002`, ... (auto-incremented with `AtomicInteger`)
- Harvests: `COS001`, `COS002`, ... (auto-incremented with `AtomicInteger`)
- IDs are generated in repository layer using `generateNextId()` method

### Query Parameter Routing

Controllers use Spring's parameter-based routing for search operations:

```java
@GetMapping(params = "tipo")           // /api/productos?tipo=Café
@GetMapping(params = "nombre")         // /api/productos?nombre=Premium
@GetMapping(params = {"hectareasMin", "hectareasMax"})  // Range queries
```

This pattern keeps the controller clean and routes specific queries to dedicated methods.

### Data Initialization

`ProductoAgricolaRepositoryImpl` and `CosechaRepositoryImpl` initialize with test data:
- 3 products: Café, Arroz, Cacao
- 5 harvests: various harvests linked to products

This happens in the constructor via `inicializarDatosPrueba()` method.

### Timezone Configuration

All dates use Colombia timezone (`America/Bogota`) configured in `application.properties`. Jackson is configured to format dates as `yyyy-MM-dd'T'HH:mm:ss`.

**Configuration properties:**
- `spring.jackson.time-zone=America/Bogota`
- `spring.jackson.date-format=yyyy-MM-dd'T'HH:mm:ss`
- `app.timezone=America/Bogota`

## Development Guidelines

### Adding New Endpoints

1. Add method signature to service interface
2. Implement business logic in service implementation
3. Add repository method if data access is needed
4. Create controller endpoint with proper validation
5. Return `ApiResponseDTO` for consistency

### When Modifying Master-Detail Logic

- Always verify `productoId` exists before creating/updating `Cosecha`
- When accessing nested endpoints, verify the cosecha belongs to the specified product
- The nested routes in `ProductoAgricolaController` enforce referential integrity

### Validation

Use Bean Validation annotations on models:
- `@NotNull`, `@NotBlank` for required fields
- `@Min`, `@Max`, `@DecimalMin` for numeric ranges
- `@Size` for string length
- `@Valid` in controller to trigger automatic validation

### Exception Handling

Custom exceptions in `exception/` package:
- `ProductoNotFoundException`
- `ProductoAlreadyExistsException`
- `CosechaNotFoundException`
- `ValidationException`

All handled by `GlobalExceptionHandler` which returns standardized error responses.

### CORS Configuration

Configured in `WebConfig.java` using externalized properties from `application.properties`. CORS settings can be easily modified in the properties file without changing code:
- `cors.allowed-origins` - Allowed origins (comma-separated)
- `cors.allowed-methods` - Allowed HTTP methods
- `cors.allowed-headers` - Allowed request headers
- `cors.allow-credentials` - Allow credentials (cookies, auth)
- `cors.max-age` - Preflight cache duration

Default settings allow all origins for academic purposes. Restrict in production.

## Key Files

### Configuration Files
- `pom.xml` - Maven dependencies and build configuration
- `application.properties` - Main application configuration (centralized)
- `application-dev.properties` - Development profile settings
- `application-prod.properties` - Production profile settings
- `CONFIGURATION.md` - Complete configuration guide

### Java Source Files
- `ProductoAgricolaController.java` - Main controller with nested cosecha routes
- `CosechaController.java` - Direct cosecha operations
- `GlobalExceptionHandler.java` - Centralized exception handling
- `ResponseBuilder.java` - Utility for building standardized responses
- `Validador.java` - Custom validation utilities
- `Constantes.java` - Application constants
- `WebConfig.java` - CORS configuration (reads from properties)

## Common Development Patterns

### Creating a New Entity

If you need to add a new entity (e.g., Finca, Trabajador):

1. Create model class in `model/` with validation annotations
2. Create repository interface in `repository/`
3. Create repository implementation with `ConcurrentHashMap` storage
4. Create service interface in `service/`
5. Create service implementation in `service/impl/`
6. Create controller in `controller/` with standardized responses
7. Consider if it needs master-detail relationship with existing entities

### Search/Filter Operations

Use Spring's parameter-based routing pattern shown in existing controllers. This keeps URL paths clean and allows multiple search methods on the same base endpoint.

## Important Notes

- This project uses **MySQL database with JPA/Hibernate** for persistence
- IDs are auto-generated and should not be provided in POST requests
- The master-detail relationship is enforced at the service layer and database level
- **All configuration is centralized in `application.properties`** - change port, database, CORS, logging, etc. in one place
- Port 8081 (dev) must be available for the application to start (configurable via `server.port`)
- Timezone is configured in `application.properties` (`America/Bogota` for Colombia)
- See `CONFIGURATION.md` for detailed configuration instructions
