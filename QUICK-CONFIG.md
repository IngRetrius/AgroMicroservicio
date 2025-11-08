# Quick Configuration Reference

## Common Configuration Tasks

### 1. Change Server Port

**File:** `application.properties`

```properties
# Default: 8081
server.port=8082
```

**Or via command line:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

---

### 2. Change Database Connection

**File:** `application.properties`

```properties
# Change host from localhost to another server
spring.datasource.url=jdbc:mysql://192.168.1.100:3306/agropecuario_db?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true

# Change database name
spring.datasource.url=jdbc:mysql://localhost:3306/my_database?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true

# Change credentials
spring.datasource.username=myuser
spring.datasource.password=mypassword
```

---

### 3. Allow Frontend CORS Access

**Development (allow all):**

```properties
cors.allowed-origins=*
cors.allow-credentials=false
```

**Production (restrict to your domain):**

```properties
cors.allowed-origins=https://myapp.com,https://www.myapp.com
cors.allow-credentials=true
```

**Local development with React/Angular:**

```properties
cors.allowed-origins=http://localhost:3000,http://localhost:4200
cors.allow-credentials=false
```

---

### 4. Change Logging Level

**File:** `application.properties`

```properties
# Show less detail (production)
logging.level.co.unibague.agropecuario=INFO
logging.level.org.springframework.web=WARN

# Show more detail (debugging)
logging.level.co.unibague.agropecuario=DEBUG
logging.level.org.springframework.web=DEBUG

# Show SQL queries
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

### 5. Change Database Schema Management

**File:** `application.properties`

```properties
# Let Hibernate update schema automatically (development)
spring.jpa.hibernate.ddl-auto=update

# Validate schema only, don't change it (production)
spring.jpa.hibernate.ddl-auto=validate

# Create fresh schema every time (testing)
spring.jpa.hibernate.ddl-auto=create-drop
```

---

### 6. Disable SQL Logging

**File:** `application.properties`

```properties
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.use_sql_comments=false
```

---

### 7. Change Timezone

**File:** `application.properties`

```properties
# For Colombia
spring.jackson.time-zone=America/Bogota
app.timezone=America/Bogota

# For other locations
spring.jackson.time-zone=America/New_York
app.timezone=America/New_York

# UTC
spring.jackson.time-zone=UTC
app.timezone=UTC
```

---

### 8. Expose More Actuator Endpoints

**File:** `application.properties`

```properties
# Default (minimal)
management.endpoints.web.exposure.include=health,info

# Expose all endpoints (development only!)
management.endpoints.web.exposure.include=*

# Expose specific endpoints
management.endpoints.web.exposure.include=health,info,metrics,env
```

---

### 9. Use Different Profiles

**Development:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Custom profile:**
1. Create `application-custom.properties`
2. Run: `mvn spring-boot:run -Dspring-boot.run.profiles=custom`

---

### 10. Override Properties via Environment Variables

**Windows:**
```cmd
set SERVER_PORT=9090
set SPRING_DATASOURCE_URL=jdbc:mysql://other-host:3306/agropecuario_db
mvn spring-boot:run
```

**Linux/Mac:**
```bash
export SERVER_PORT=9090
export SPRING_DATASOURCE_URL=jdbc:mysql://other-host:3306/agropecuario_db
mvn spring-boot:run
```

**Format:** Replace `.` with `_` and uppercase everything
- `server.port` → `SERVER_PORT`
- `spring.datasource.url` → `SPRING_DATASOURCE_URL`

---

## Profile-Specific Files

### application-dev.properties (Development)
- Port: 8081
- Database: localhost
- CORS: Allow all (*)
- Logging: DEBUG
- SQL logging: Enabled
- Schema: Auto-update

### application-prod.properties (Production)
- Port: 8080
- Database: Production server
- CORS: Restricted domains
- Logging: INFO/WARN
- SQL logging: Disabled
- Schema: Validate only

---

## Configuration Priority

Properties are loaded in this order (last wins):

1. `application.properties` (base)
2. `application-{profile}.properties` (profile-specific)
3. Environment variables
4. Command-line arguments

**Example:**
```bash
# Port will be 9000 (command line overrides file)
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9000
```

---

## Testing Configuration Changes

After changing configuration:

1. **Clean and compile:**
   ```bash
   mvn clean compile
   ```

2. **Run application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Verify port:**
   Check console output for: `Tomcat started on port(s): 8081`

4. **Test endpoint:**
   ```bash
   curl http://localhost:8081/actuator/health
   ```

---

## Common Issues

### Port Already in Use
**Error:** `Port 8081 is already in use`

**Solution:** Change port or kill the process
```properties
server.port=8082
```

### Database Connection Failed
**Error:** `Communications link failure`

**Solution:** Check MySQL is running
```bash
# Windows
net start MySQL

# Linux
sudo systemctl start mysql
```

### CORS Error
**Error:** `No 'Access-Control-Allow-Origin' header`

**Solution:** Add your frontend URL to allowed origins
```properties
cors.allowed-origins=http://localhost:3000
```

---

## Need More Help?

See the complete configuration guide: **[CONFIGURATION.md](CONFIGURATION.md)**
