# Configuration Guide - Agropecuario REST API

This document explains how to configure the Agropecuario REST API using the centralized `application.properties` files.

## Configuration Files

The application uses Spring Boot's properties-based configuration system with profile-specific overrides:

- **`application.properties`** - Base configuration (shared across all profiles)
- **`application-dev.properties`** - Development-specific settings
- **`application-prod.properties`** - Production-specific settings

## Quick Configuration Changes

### 1. Change Server Port

Edit `application.properties` or profile-specific file:

```properties
server.port=8081
```

**Common ports:**
- Development: `8081` (default)
- Production: `8080` (recommended)

### 2. Change Database Settings

Edit the database connection in `application.properties`:

```properties
# Database host and port
spring.datasource.url=jdbc:mysql://localhost:3306/agropecuario_db?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true

# Database credentials
spring.datasource.username=agropecuario_user
spring.datasource.password=agropecuario_password
```

**Production example:**
```properties
spring.datasource.url=jdbc:mysql://prod-server:3306/agropecuario_db?useSSL=true&serverTimezone=America/Bogota
spring.datasource.username=prod_user
spring.datasource.password=SecurePassword123!
```

### 3. Change CORS Settings

Edit CORS configuration in `application.properties`:

```properties
# Allow all origins (development)
cors.allowed-origins=*

# Restrict to specific domains (production)
cors.allowed-origins=https://yourdomain.com,https://app.yourdomain.com

# Allowed HTTP methods
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH

# Allow credentials (cookies, auth headers)
cors.allow-credentials=false
```

### 4. Change Logging Level

Edit logging levels in `application.properties`:

```properties
# Application logging
logging.level.co.unibague.agropecuario=DEBUG

# Spring framework logging
logging.level.org.springframework.web=DEBUG

# SQL logging (uncomment to enable)
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**Logging levels:** `TRACE` < `DEBUG` < `INFO` < `WARN` < `ERROR`

### 5. Change Hibernate DDL Strategy

Edit JPA settings in `application.properties`:

```properties
# Options: none, validate, update, create, create-drop
spring.jpa.hibernate.ddl-auto=update
```

**Strategies:**
- **`none`** - No schema management
- **`validate`** - Validate schema, don't make changes (production)
- **`update`** - Update schema if needed (development)
- **`create`** - Create schema, drop existing data
- **`create-drop`** - Create on startup, drop on shutdown (testing)

### 6. Change Timezone

Edit timezone in `application.properties`:

```properties
spring.jackson.time-zone=America/Bogota
app.timezone=America/Bogota
```

### 7. Change Actuator Endpoints

Edit actuator settings in `application.properties`:

```properties
# Expose specific endpoints
management.endpoints.web.exposure.include=health,info

# Expose all endpoints (not recommended for production)
management.endpoints.web.exposure.include=*
```

## Running with Different Profiles

### Development Profile (Default)

```bash
# Using Maven
mvn spring-boot:run

# Using Maven with explicit profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Using packaged JAR
java -jar target/agropecuario-rest-server-1.0.0.jar --spring.profiles.active=dev
```

### Production Profile

```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Using packaged JAR
java -jar target/agropecuario-rest-server-1.0.0.jar --spring.profiles.active=prod
```

## Configuration Hierarchy

Spring Boot loads properties in this order (later sources override earlier ones):

1. `application.properties` (base configuration)
2. `application-{profile}.properties` (profile-specific)
3. Environment variables
4. Command-line arguments

### Example: Override Port via Command Line

```bash
java -jar target/agropecuario-rest-server-1.0.0.jar --server.port=9090
```

### Example: Override via Environment Variable

```bash
# Windows
set SERVER_PORT=9090
java -jar target/agropecuario-rest-server-1.0.0.jar

# Linux/Mac
export SERVER_PORT=9090
java -jar target/agropecuario-rest-server-1.0.0.jar
```

## All Configurable Properties

### Server Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8081` | HTTP port |
| `server.servlet.context-path` | `/` | Base path for all endpoints |

### Database Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/agropecuario_db...` | JDBC connection URL |
| `spring.datasource.username` | `agropecuario_user` | Database username |
| `spring.datasource.password` | `agropecuario_password` | Database password |
| `spring.datasource.driver-class-name` | `com.mysql.cj.jdbc.Driver` | JDBC driver |
| `spring.datasource.hikari.maximum-pool-size` | `10` | Max connection pool size |
| `spring.datasource.hikari.minimum-idle` | `5` | Min idle connections |
| `spring.datasource.hikari.connection-timeout` | `20000` | Connection timeout (ms) |

### JPA/Hibernate Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema generation strategy |
| `spring.jpa.show-sql` | `true` | Show SQL in logs |
| `spring.jpa.properties.hibernate.dialect` | `MySQLDialect` | SQL dialect |
| `spring.jpa.properties.hibernate.format_sql` | `true` | Format SQL output |
| `spring.jpa.open-in-view` | `false` | Open Session In View pattern |

### Jackson Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `spring.jackson.time-zone` | `America/Bogota` | Timezone for date serialization |
| `spring.jackson.date-format` | `yyyy-MM-dd'T'HH:mm:ss` | Date format pattern |
| `spring.jackson.serialization.write-dates-as-timestamps` | `false` | Use ISO format, not timestamps |

### CORS Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `cors.allowed-origins` | `*` | Allowed origins (comma-separated) |
| `cors.allowed-methods` | `GET,POST,PUT,DELETE,OPTIONS,PATCH` | Allowed HTTP methods |
| `cors.allowed-headers` | `*` | Allowed headers |
| `cors.allow-credentials` | `false` | Allow credentials |
| `cors.exposed-headers` | `Authorization,Content-Type` | Exposed headers |
| `cors.max-age` | `3600` | Preflight cache duration (seconds) |
| `cors.api-path` | `/api/**` | API path pattern |
| `cors.actuator-path` | `/actuator/**` | Actuator path pattern |

### Logging Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `logging.level.co.unibague.agropecuario` | `DEBUG` | Application logging level |
| `logging.level.org.springframework.web` | `DEBUG` | Spring Web logging level |

### Actuator Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `management.endpoints.web.exposure.include` | `health,info` | Exposed actuator endpoints |

### Application Business Logic

| Property | Default | Description |
|----------|---------|-------------|
| `app.producto.min-hectareas` | `0.1` | Minimum hectares |
| `app.producto.max-hectareas` | `10000.0` | Maximum hectares |
| `app.producto.min-precio` | `100.0` | Minimum price |
| `app.producto.max-precio` | `1000000.0` | Maximum price |
| `app.id.producto-prefix` | `AGR` | Product ID prefix |
| `app.id.cosecha-prefix` | `COS` | Harvest ID prefix |
| `app.id.padding` | `3` | ID number padding |
| `app.timezone` | `America/Bogota` | Application timezone |

## Best Practices

### Development
- Use `application-dev.properties` for local settings
- Keep `ddl-auto=update` to auto-update schema
- Enable SQL logging for debugging
- Allow all CORS origins (`*`)

### Production
- Use `application-prod.properties`
- Set `ddl-auto=validate` to prevent schema changes
- Disable SQL logging (`show-sql=false`)
- Restrict CORS to specific domains
- Use environment variables for sensitive data (passwords)
- Use connection pooling settings

### Security
- **Never commit production passwords** to version control
- Use environment variables or external configuration for sensitive data
- Restrict CORS origins in production
- Use SSL/TLS for database connections in production

## Troubleshooting

### Port Already in Use

**Error:** `Port 8081 is already in use`

**Solution:** Change the port in `application.properties`:
```properties
server.port=8082
```

### Database Connection Failed

**Error:** `Communications link failure`

**Solution:** Verify database is running and check connection settings:
```bash
# Test MySQL connection
mysql -h localhost -P 3306 -u agropecuario_user -p
```

### CORS Errors

**Error:** `CORS policy: No 'Access-Control-Allow-Origin' header`

**Solution:** Add your frontend domain to allowed origins:
```properties
cors.allowed-origins=http://localhost:3000,http://localhost:4200
```

## Migration from YAML

The previous `application.yml` files have been converted to `.properties` format. If you need to switch back to YAML:

1. Rename `application.properties` to `application.yml`
2. Convert properties to YAML format
3. Delete the `.properties` files

However, `.properties` format is recommended for easier editing and better IDE support.

## Environment-Specific Configuration Example

### Local Development
Create `application-local.properties`:
```properties
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/agropecuario_local
```

Run with:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Docker Environment
Create `application-docker.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://mysql-container:3306/agropecuario_db
```

## External Configuration

For production, you can use external configuration files:

```bash
# Linux/Mac
java -jar app.jar --spring.config.location=/etc/agropecuario/application.properties

# Windows
java -jar app.jar --spring.config.location=C:\config\application.properties
```

## Getting Help

For Spring Boot configuration documentation:
- [Spring Boot Common Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
- [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
