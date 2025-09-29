# Sistema Agropecuario - Microservicio REST

API REST para la gestión de productos agrícolas desarrollada con Spring Boot.

## Descripción

Microservicio que expone operaciones CRUD para productos agrícolas a través de servicios web REST. Implementa una arquitectura en capas con persistencia en memoria.

## Tecnologías

- Java 17
- Spring Boot 3.x
- Maven
- Jackson (JSON)
- Bean Validation
- Spring Web

## Requisitos Previos

- Java JDK 17 o superior
- Maven 3.6 o superior
- Puerto 8081 disponible

## Instalación

1. Clonar el repositorio
```bash
git clone [url-repositorio]
cd agropecuario-rest
```

2. Compilar el proyecto
```bash
mvn clean compile
```


## Ejecución

### Desarrollo
```bash
mvn spring-boot:run
```

### Producción
```bash
mvn clean package
java -jar target/agropecuario-rest-1.0.0.jar
```

El servidor iniciará en: `http://localhost:8081`

## Endpoints de la API

### Productos
- `GET /api/productos` - Listar todos los productos
- `GET /api/productos/{id}` - Obtener producto por ID
- `POST /api/productos` - Crear nuevo producto
- `PUT /api/productos/{id}` - Actualizar producto
- `DELETE /api/productos/{id}` - Eliminar producto

### Búsquedas
- `GET /api/productos?tipo={tipo}` - Filtrar por tipo de cultivo
- `GET /api/productos?nombre={nombre}` - Buscar por nombre
- `GET /api/productos?temporada={temporada}` - Filtrar por temporada
- `GET /api/productos?hectareasMin={min}&hectareasMax={max}` - Rango hectáreas

### Utilitarios
- `GET /api/productos/estadisticas` - Estadísticas del sistema
- `GET /actuator/health` - Estado del servicio

## Ejemplo de Uso

### Crear Producto
```bash
curl -X POST http://localhost:8081/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Café Premium",
    "tipoCultivo": "Café",
    "hectareasCultivadas": 5.5,
    "cantidadProducida": 1200,
    "precioVenta": 8500,
    "costoProduccion": 2800000,
    "temporada": "Cosecha principal",
    "tipoSuelo": "Franco arcilloso",
    "codigoFinca": "F001"
  }'
```

### Respuesta Exitosa
```json
{
  "success": true,
  "message": "Producto creado exitosamente",
  "data": {
    "id": "AGR004",
    "nombre": "Café Premium",
    "tipoCultivo": "Café",
    "hectareasCultivadas": 5.5,
    "cantidadProducida": 1200,
    "fechaProduccion": "2025-09-28T10:30:00",
    "precioVenta": 8500.0,
    "costoProduccion": 2800000.0,
    "temporada": "Cosecha principal",
    "tipoSuelo": "Franco arcilloso",
    "codigoFinca": "F001"
  },
  "timestamp": "2025-09-28T10:30:00"
}
```

## Estructura del Proyecto

```
src/main/java/co/unibague/agropecuario/rest/
├── AgropecuarioRestApplication.java
├── config/
│   └── WebConfig.java
├── controller/
│   ├── GlobalExceptionHandler.java
│   └── ProductoAgricolaController.java
├── dto/
│   ├── ApiResponseDTO.java
│   ├── BusquedaRequestDTO.java
│   ├── ErrorResponseDTO.java
│   └── ProductoAgricolaDTO.java
├── exception/
│   ├── ProductoAlreadyExistsException.java
│   ├── ProductoNotFoundException.java
│   └── ValidationException.java
├── model/
│   └── ProductoAgricola.java
├── repository/
│   ├── ProductoAgricolaRepository.java
│   └── impl/
│       └── ProductoAgricolaRepositoryImpl.java
├── service/
│   ├── ProductoAgricolaService.java
│   └── impl/
│       └── ProductoAgricolaServiceImpl.java
└── utils/
    ├── Constantes.java
    ├── ResponseBuilder.java
    └── Validador.java
```

## Configuración

### application.yml
```yaml
server:
  port: 8081

spring:
  application:
    name: agropecuario-rest-api
  jackson:
    time-zone: America/Bogota
    date-format: yyyy-MM-dd'T'HH:mm:ss

logging:
  level:
    co.unibague.agropecuario: DEBUG
```

## Datos de Prueba

El sistema inicializa automáticamente con 3 productos:
- AGR001: Café Arábica Premium
- AGR002: Arroz Fedearroz 67
- AGR003: Cacao Trinitario

## Validaciones

### Campos Obligatorios
- nombre (2-100 caracteres)
- tipoCultivo
- hectareasCultivadas (0.1 - 10,000)
- cantidadProducida (1 - 1,000,000)
- precioVenta (100 - 1,000,000)
- costoProduccion (100+)

### Códigos de Estado HTTP
- 200: Operación exitosa
- 201: Recurso creado
- 400: Datos inválidos
- 404: Recurso no encontrado
- 500: Error interno del servidor

## CORS

El microservicio está configurado para aceptar peticiones desde cualquier origen:
- Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS
- Headers permitidos: *

## Monitoreo

### Health Check
```bash
curl http://localhost:8081/actuator/health
```

### Estadísticas
```bash
curl http://localhost:8081/api/productos/estadisticas
```

## Desarrollo

### Agregar Nuevos Endpoints
1. Crear método en `ProductoAgricolaController`
2. Implementar lógica en `ProductoAgricolaService`
3. Actualizar repository si es necesario

### Cambiar Persistencia
1. Implementar nueva clase repository
2. Configurar datasource en application.yml
3. Agregar dependencias JPA/Hibernate en pom.xml

## Contribución

Universidad de Ibagué - Facultad de Ingeniería  
Desarrollo de Aplicaciones Empresariales  
Segundo Taller 2025B
Juan Camilo Perea Possos

## Licencia

Proyecto académico - Universidad de Ibagué