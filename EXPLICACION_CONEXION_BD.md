# Explicación Detallada: Conexión y Funcionamiento con Base de Datos MySQL

## Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
### Tercer Prototipo 2025B - Sistema de Gestión Agropecuaria

---

## 📚 Índice

1. [Arquitectura General](#1-arquitectura-general)
2. [Configuración de la Conexión](#2-configuración-de-la-conexión)
3. [Mapeo Objeto-Relacional (ORM)](#3-mapeo-objeto-relacional-orm)
4. [Relación Maestro-Detalle](#4-relación-maestro-detalle)
5. [Repositorios JPA](#5-repositorios-jpa)
6. [Capa de Servicio](#6-capa-de-servicio)
7. [Flujo Completo de una Petición](#7-flujo-completo-de-una-petición)
8. [Consultas Personalizadas](#8-consultas-personalizadas)
9. [Gestión de Transacciones](#9-gestión-de-transacciones)
10. [Pool de Conexiones](#10-pool-de-conexiones)

---

## 1. Arquitectura General

### 1.1 Diagrama de Capas

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENTE REST                          │
│            (Postman, curl, aplicación web)               │
└─────────────────────────────────────────────────────────┘
                           │
                           │ HTTP Request
                           ▼
┌─────────────────────────────────────────────────────────┐
│              CAPA CONTROLLER (REST)                      │
│         ProductoAgricolaController.java                  │
│         CosechaController.java                           │
│  - Recibe peticiones HTTP                               │
│  - Valida datos con @Valid                              │
│  - Devuelve ApiResponseDTO                              │
└─────────────────────────────────────────────────────────┘
                           │
                           │ Llamada a métodos
                           ▼
┌─────────────────────────────────────────────────────────┐
│              CAPA SERVICE (Lógica de negocio)            │
│         ProductoAgricolaServiceImpl.java                 │
│         CosechaServiceImpl.java                          │
│  - Lógica de negocio                                    │
│  - Validaciones complejas                               │
│  - Manejo de transacciones (@Transactional)             │
└─────────────────────────────────────────────────────────┘
                           │
                           │ Operaciones CRUD
                           ▼
┌─────────────────────────────────────────────────────────┐
│            CAPA REPOSITORY (Acceso a datos)              │
│         ProductoAgricolaJpaRepository.java               │
│         CosechaJpaRepository.java                        │
│  - Extiende JpaRepository                               │
│  - Consultas automáticas (findById, save, etc.)         │
│  - Consultas personalizadas (@Query)                    │
└─────────────────────────────────────────────────────────┘
                           │
                           │ SQL Queries
                           ▼
┌─────────────────────────────────────────────────────────┐
│                     HIBERNATE/JPA                        │
│  - ORM (Object-Relational Mapping)                      │
│  - Traduce objetos Java a SQL                           │
│  - Gestiona el ciclo de vida de entidades               │
│  - Cache de primer nivel                                │
└─────────────────────────────────────────────────────────┘
                           │
                           │ JDBC Connection
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    HIKARICP (Pool)                       │
│  - Administra conexiones a la BD                        │
│  - Reutiliza conexiones (performance)                   │
│  - Configura timeouts                                   │
└─────────────────────────────────────────────────────────┘
                           │
                           │ TCP/IP Connection
                           ▼
┌─────────────────────────────────────────────────────────┐
│                  BASE DE DATOS MYSQL                     │
│         agropecuario_db                                  │
│  Tablas:                                                │
│  - producto_agricola (maestro)                          │
│  - cosecha (detalle)                                    │
└─────────────────────────────────────────────────────────┘
```

---

## 2. Configuración de la Conexión

### 2.1 Archivo: `application.yml`

**Ubicación:** `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: agropecuario-rest-api

  # ===== CONFIGURACIÓN DE LA BASE DE DATOS =====
  datasource:
    # URL de conexión JDBC
    url: jdbc:mysql://localhost:3306/agropecuario_db?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true

    # Credenciales
    username: agropecuario_user
    password: agropecuario_password

    # Driver JDBC de MySQL
    driver-class-name: com.mysql.cj.jdbc.Driver

  # ===== CONFIGURACIÓN DE JPA/HIBERNATE =====
  jpa:
    hibernate:
      # DDL Auto: update = actualiza el esquema sin borrar datos
      ddl-auto: update

    # Mostrar SQL en consola (útil para debug)
    show-sql: true

    properties:
      hibernate:
        # Dialecto específico de MySQL
        dialect: org.hibernate.dialect.MySQLDialect

        # Formatear SQL en consola
        format_sql: true

        # Mostrar comentarios en SQL
        use_sql_comments: true

    # Desactivar Open Session In View (mejores prácticas)
    open-in-view: false
```

### 2.2 ¿Qué significa cada parámetro?

#### URL de Conexión JDBC:
```
jdbc:mysql://localhost:3306/agropecuario_db?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true
```

- **`jdbc:mysql://`** - Protocolo JDBC para MySQL
- **`localhost:3306`** - Host y puerto del servidor MySQL
- **`agropecuario_db`** - Nombre de la base de datos
- **`useSSL=false`** - Desactiva SSL (solo para desarrollo local)
- **`serverTimezone=America/Bogota`** - Zona horaria de Colombia
- **`allowPublicKeyRetrieval=true`** - Permite autenticación con clave pública

#### Hibernate DDL Auto:
- **`update`**: Actualiza el esquema automáticamente sin borrar datos
- **`create`**: Recrea las tablas (BORRA DATOS)
- **`validate`**: Solo valida que el esquema coincida
- **`none`**: No hace nada automáticamente

---

## 3. Mapeo Objeto-Relacional (ORM)

### 3.1 Entidad ProductoAgricola (Maestro)

**Ubicación:** `src/main/java/co/unibague/agropecuario/rest/model/ProductoAgricola.java`

```java
@Entity  // Marca esta clase como una entidad JPA
@Table(name = "producto_agricola")  // Nombre de la tabla en MySQL
public class ProductoAgricola {

    // ===== LLAVE PRIMARIA =====
    @Id  // Indica que este campo es la llave primaria
    @Column(name = "id", length = 10, nullable = false)
    private String id;  // Se mapea a la columna 'id' en la BD

    // ===== CAMPOS OBLIGATORIOS =====
    @Column(name = "nombre", length = 100, nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(name = "hectareas_cultivadas", nullable = false)
    @NotNull(message = "Las hectáreas cultivadas son obligatorias")
    private Double hectareasCultivadas;

    @Column(name = "cantidad_producida", nullable = false)
    @NotNull(message = "La cantidad producida es obligatoria")
    private Integer cantidadProducida;

    @Column(name = "tipo_cultivo", length = 50, nullable = false)
    @NotBlank(message = "El tipo de cultivo es obligatorio")
    private String tipoCultivo;

    // ===== CAMPOS OPCIONALES =====
    @Column(name = "temporada", length = 50)
    private String temporada;

    @Column(name = "tipo_suelo", length = 50)
    private String tipoSuelo;

    // ... getters, setters, constructores ...
}
```

#### ¿Cómo funciona el mapeo?

1. **`@Entity`**: Le dice a JPA que esta clase representa una tabla
2. **`@Table`**: Especifica el nombre exacto de la tabla en MySQL
3. **`@Id`**: Marca el campo como llave primaria
4. **`@Column`**: Mapea el atributo Java a una columna específica
   - `name`: Nombre de la columna en la BD
   - `length`: Longitud máxima (para VARCHAR)
   - `nullable`: Si puede ser NULL o no

#### Tabla resultante en MySQL:

```sql
CREATE TABLE producto_agricola (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    hectareas_cultivadas DOUBLE NOT NULL,
    cantidad_producida INT NOT NULL,
    tipo_cultivo VARCHAR(50) NOT NULL,
    temporada VARCHAR(50),
    tipo_suelo VARCHAR(50),
    ...
);
```

### 3.2 Entidad Cosecha (Detalle)

**Ubicación:** `src/main/java/co/unibague/agropecuario/rest/model/Cosecha.java`

```java
@Entity
@Table(name = "cosecha")
public class Cosecha {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    // ===== RELACIÓN MANY-TO-ONE CON PRODUCTO =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "producto_id",           // Nombre de la columna FK en la BD
        nullable = false,                // No puede ser NULL
        foreignKey = @ForeignKey(name = "fk_cosecha_producto")  // Nombre del constraint
    )
    @JsonIgnore  // No serializar el objeto completo en JSON
    private ProductoAgricola producto;

    // ===== CAMPO TRANSIENT PARA API REST =====
    @Transient  // No se persiste en la BD, solo existe en Java
    @JsonProperty("productoId")
    private String productoId;

    // Getter que obtiene el ID del producto relacionado
    public String getProductoId() {
        if (producto != null) {
            return producto.getId();
        }
        return productoId;
    }

    // Setter que establece la relación
    public void setProducto(ProductoAgricola producto) {
        this.producto = producto;
        if (producto != null) {
            this.productoId = producto.getId();
        }
    }

    // ... otros campos y métodos ...
}
```

#### Explicación de la relación @ManyToOne:

```
┌──────────────────────┐         ┌──────────────────────┐
│  ProductoAgricola    │ 1     N │      Cosecha         │
│  (MAESTRO)           │─────────│      (DETALLE)       │
│                      │         │                      │
│  id (PK)             │◄────────│  producto_id (FK)    │
│  nombre              │         │  fecha_cosecha       │
│  tipo_cultivo        │         │  cantidad_recolectada│
│  ...                 │         │  ...                 │
└──────────────────────┘         └──────────────────────┘
```

- **`@ManyToOne`**: Muchas cosechas pertenecen a UN producto
- **`fetch = FetchType.LAZY`**: No carga el producto automáticamente (mejora performance)
- **`@JoinColumn`**: Define la columna de la foreign key
- **`@JsonIgnore`**: Evita recursión infinita en JSON

---

## 4. Relación Maestro-Detalle

### 4.1 Cómo funciona en el código

#### Cuando creamos una cosecha:

**Archivo:** `CosechaServiceImpl.java` (líneas 38-66)

```java
@Override
public Cosecha crearCosecha(Cosecha cosecha) {
    // 1. Validar datos de la cosecha
    validarCosecha(cosecha);

    // 2. Obtener el producto desde la BD usando el ID proporcionado
    String productoId = cosecha.getProductoId();
    ProductoAgricola producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new ProductoNotFoundException(
                    "No se puede crear la cosecha. El producto con ID '" +
                            productoId + "' no existe"));

    // 3. Establecer la relación @ManyToOne
    cosecha.setProducto(producto);  // ← ESTO ES CLAVE

    // 4. Generar ID si no lo tiene
    if (cosecha.getId() == null || cosecha.getId().trim().isEmpty()) {
        cosecha.setId(idGenerator.generateNextCosechaId());
    }

    // 5. Guardar en la BD (Hibernate genera el INSERT)
    return cosechaRepository.save(cosecha);
}
```

#### SQL generado por Hibernate:

```sql
-- Paso 1: Verificar que el producto existe
SELECT * FROM producto_agricola WHERE id = 'AGR001';

-- Paso 2: Insertar la cosecha con la FK
INSERT INTO cosecha (
    id,
    producto_id,  -- ← Foreign Key
    fecha_cosecha,
    cantidad_recolectada,
    calidad_producto,
    ...
) VALUES (
    'COS001',
    'AGR001',  -- ← Referencia al producto
    '2025-04-10 07:00:00',
    2800,
    'Premium',
    ...
);
```

### 4.2 Integridad Referencial

La base de datos garantiza que:

```sql
-- Constraint en la tabla cosecha
CONSTRAINT fk_cosecha_producto
    FOREIGN KEY (producto_id)
    REFERENCES producto_agricola(id)
    ON DELETE CASCADE      -- Si borras el producto, borra sus cosechas
    ON UPDATE CASCADE      -- Si actualizas el ID del producto, actualiza las FKs
```

**Esto significa que:**
- ❌ No puedes crear una cosecha con un `producto_id` que no existe
- ❌ No puedes eliminar un producto que tiene cosechas sin borrar las cosechas primero (o usar CASCADE)
- ✅ La integridad de los datos está garantizada a nivel de base de datos

---

## 5. Repositorios JPA

### 5.1 ProductoAgricolaJpaRepository

**Ubicación:** `src/main/java/co/unibague/agropecuario/rest/repository/ProductoAgricolaJpaRepository.java`

```java
@Repository
public interface ProductoAgricolaJpaRepository
    extends JpaRepository<ProductoAgricola, String> {
    //              ↑ Entidad         ↑ Tipo de la PK

    // ===== MÉTODOS AUTOMÁTICOS (heredados de JpaRepository) =====
    // save(entity)           → INSERT o UPDATE
    // findById(id)           → SELECT WHERE id = ?
    // findAll()              → SELECT * FROM ...
    // deleteById(id)         → DELETE WHERE id = ?
    // existsById(id)         → SELECT COUNT(*) WHERE id = ?
    // count()                → SELECT COUNT(*) FROM ...

    // ===== QUERY METHODS (Spring Data los implementa automáticamente) =====

    // Spring Data JPA crea automáticamente:
    // SELECT * FROM producto_agricola WHERE nombre = ?
    Optional<ProductoAgricola> findByNombre(String nombre);

    // SELECT * FROM producto_agricola WHERE tipo_cultivo = ?
    List<ProductoAgricola> findByTipoCultivo(String tipoCultivo);

    // SELECT * FROM producto_agricola
    // WHERE hectareas_cultivadas BETWEEN ? AND ?
    List<ProductoAgricola> findByHectareasCultivadasBetween(
        Double min, Double max
    );

    // ===== CONSULTAS PERSONALIZADAS CON @Query =====

    @Query("SELECT p FROM ProductoAgricola p " +
           "WHERE (p.cantidadProducida * p.precioVenta - " +
           "p.costoProduccion * p.hectareasCultivadas) / " +
           "p.hectareasCultivadas > :rentabilidadMinima")
    List<ProductoAgricola> findProductosConRentabilidadSuperiorA(
        @Param("rentabilidadMinima") Double rentabilidadMinima
    );
}
```

#### ¿Cómo funciona Spring Data JPA?

1. **Métodos automáticos:** Solo defines la interfaz, Spring implementa todo
2. **Query Methods:** Spring analiza el nombre del método y genera el SQL
3. **@Query:** Escribes JPQL (Java Persistence Query Language) manualmente

#### Ejemplo de uso en el servicio:

```java
// En ProductoAgricolaServiceImpl.java

@Autowired
private ProductoAgricolaJpaRepository repository;

public List<ProductoAgricola> obtenerTodosLosProductos() {
    // Spring Data JPA ejecuta: SELECT * FROM producto_agricola
    return repository.findAll();
}

public Optional<ProductoAgricola> obtenerProductoPorId(String id) {
    // Spring Data JPA ejecuta: SELECT * FROM producto_agricola WHERE id = ?
    return repository.findById(id);
}

public ProductoAgricola crearProducto(ProductoAgricola producto) {
    // Spring Data JPA ejecuta: INSERT INTO producto_agricola (...)
    return repository.save(producto);
}
```

---

## 6. Capa de Servicio

### 6.1 ProductoAgricolaServiceImpl

**Ubicación:** `src/main/java/co/unibague/agropecuario/rest/service/impl/ProductoAgricolaServiceImpl.java`

```java
@Service
@Transactional  // ← Todas las operaciones son transaccionales
public class ProductoAgricolaServiceImpl implements ProductoAgricolaService {

    @Autowired
    private ProductoAgricolaJpaRepository repository;

    @Autowired
    private IdGeneratorService idGenerator;

    @Override
    public ProductoAgricola crearProducto(ProductoAgricola producto) {
        // 1. Validar datos del producto
        validarProducto(producto);

        // 2. Generar ID si no lo tiene
        if (producto.getId() == null || producto.getId().trim().isEmpty()) {
            producto.setId(idGenerator.generateNextProductoId());
        } else {
            // Verificar que no exista
            if (repository.existsById(producto.getId())) {
                throw new ProductoAlreadyExistsException(
                    "Ya existe un producto con el ID: " + producto.getId()
                );
            }
        }

        // 3. Guardar en la BD
        return repository.save(producto);
        // Hibernate ejecuta: INSERT INTO producto_agricola (...)
    }

    @Override
    public ProductoAgricola actualizarProducto(String id, ProductoAgricola producto) {
        // 1. Verificar que existe
        if (!repository.existsById(id)) {
            throw new ProductoNotFoundException(
                "Producto no encontrado con ID: " + id
            );
        }

        // 2. Asegurar que el ID coincida
        producto.setId(id);

        // 3. Validar datos
        validarProducto(producto);

        // 4. Guardar (UPDATE porque el ID ya existe)
        return repository.save(producto);
        // Hibernate ejecuta: UPDATE producto_agricola SET ... WHERE id = ?
    }

    @Override
    public boolean eliminarProducto(String id) {
        if (!repository.existsById(id)) {
            throw new ProductoNotFoundException(
                "Producto no encontrado con ID: " + id
            );
        }

        repository.deleteById(id);
        // Hibernate ejecuta: DELETE FROM producto_agricola WHERE id = ?
        // Y por CASCADE: DELETE FROM cosecha WHERE producto_id = ?

        return true;
    }
}
```

### 6.2 ¿Por qué usar @Transactional?

```java
@Transactional
public ProductoAgricola crearProducto(ProductoAgricola producto) {
    // Todo este método se ejecuta en una transacción

    // Operación 1: Verificar si existe
    if (repository.existsById(producto.getId())) {
        throw new ProductoAlreadyExistsException(...);
    }

    // Operación 2: Guardar el producto
    ProductoAgricola saved = repository.save(producto);

    // Si algo falla aquí, se hace ROLLBACK automático
    // y no se guarda nada en la BD

    return saved;
    // Si todo sale bien, se hace COMMIT
}
```

**Transacciones garantizan:**
- ✅ **Atomicidad:** Todo se ejecuta o nada se ejecuta
- ✅ **Consistencia:** La BD siempre queda en un estado válido
- ✅ **Aislamiento:** Otras peticiones no ven datos intermedios
- ✅ **Durabilidad:** Una vez confirmada, la operación es permanente

---

## 7. Flujo Completo de una Petición

### 7.1 Ejemplo: GET /api/productos/AGR001/cosechas

```
┌─────────────────────────────────────────────────────────┐
│ 1. CLIENTE HACE LA PETICIÓN                             │
│    curl http://localhost:8081/api/productos/AGR001/cosechas
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 2. CONTROLLER recibe la petición                        │
│    ProductoAgricolaController.java:280                   │
└─────────────────────────────────────────────────────────┘

@GetMapping("/{id}/cosechas")
public ResponseEntity<ApiResponseDTO<List<Cosecha>>>
    obtenerCosechasDelProducto(@PathVariable String id) {

    // Llamar al servicio
    List<Cosecha> cosechas = cosechaService.obtenerCosechasPorProducto(id);

    return ResponseEntity.ok(
        ResponseBuilder.success(cosechas, mensaje)
    );
}
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 3. SERVICE ejecuta la lógica                            │
│    CosechaServiceImpl.java:79                            │
└─────────────────────────────────────────────────────────┘

@Override
@Transactional(readOnly = true)  // ← Solo lectura
public List<Cosecha> obtenerCosechasPorProducto(String productoId) {
    // Validar que el producto existe
    if (!productoRepository.existsById(productoId)) {
        throw new ProductoNotFoundException(
            "El producto con ID '" + productoId + "' no existe"
        );
    }

    // Obtener las cosechas
    return cosechaRepository.findCosechasByProductoId(productoId);
}
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 4. REPOSITORY ejecuta la consulta                       │
│    CosechaJpaRepository.java:25                          │
└─────────────────────────────────────────────────────────┘

@Query("SELECT c FROM Cosecha c WHERE c.producto.id = :productoId")
List<Cosecha> findCosechasByProductoId(@Param("productoId") String productoId);

                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 5. HIBERNATE traduce JPQL a SQL                         │
└─────────────────────────────────────────────────────────┘

-- SQL generado por Hibernate:
SELECT
    c.id,
    c.calidad_producto,
    c.cantidad_recolectada,
    c.condiciones_climaticas,
    c.costo_mano_obra,
    c.estado_cosecha,
    c.fecha_cosecha,
    c.numero_trabajadores,
    c.observaciones,
    c.producto_id
FROM cosecha c
WHERE c.producto_id = 'AGR001';

                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 6. HIKARICP proporciona una conexión del pool           │
└─────────────────────────────────────────────────────────┘

Connection conn = dataSource.getConnection();
// Conexión reutilizada del pool (más rápido)

                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 7. MYSQL ejecuta la consulta y devuelve resultados      │
└─────────────────────────────────────────────────────────┘

Resultado (2 filas):
┌────────┬─────────────┬─────────────┬──────────┐
│ id     │ producto_id │ cantidad... │ calidad  │
├────────┼─────────────┼─────────────┼──────────┤
│ COS001 │ AGR001      │ 2800        │ Premium  │
│ COS002 │ AGR001      │ 3200        │ Extra    │
└────────┴─────────────┴─────────────┴──────────┘

                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 8. HIBERNATE mapea ResultSet a objetos Java             │
└─────────────────────────────────────────────────────────┘

List<Cosecha> = [
    Cosecha{id="COS001", productoId="AGR001", cantidad=2800, ...},
    Cosecha{id="COS002", productoId="AGR001", cantidad=3200, ...}
]

                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 9. SERVICE devuelve la lista al CONTROLLER              │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 10. CONTROLLER serializa a JSON (Jackson)               │
└─────────────────────────────────────────────────────────┘

{
  "success": true,
  "message": "Se encontraron 2 cosechas para el producto 'AGR001'",
  "data": [
    {
      "id": "COS001",
      "productoId": "AGR001",
      "fechaCosecha": "2025-04-10T07:00:00",
      "cantidadRecolectada": 2800,
      "calidadProducto": "Premium",
      ...
    },
    {
      "id": "COS002",
      ...
    }
  ],
  "timestamp": "2025-10-30T16:00:32.0559069"
}

                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│ 11. RESPUESTA HTTP enviada al CLIENTE                   │
│     Status: 200 OK                                      │
│     Content-Type: application/json                      │
└─────────────────────────────────────────────────────────┘
```

---

## 8. Consultas Personalizadas

### 8.1 Consulta Simple

**Archivo:** `ProductoAgricolaJpaRepository.java`

```java
@Query("SELECT p FROM ProductoAgricola p " +
       "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
List<ProductoAgricola> buscarPorNombreContiene(@Param("nombre") String nombre);
```

**Uso:**
```java
// En el servicio
List<ProductoAgricola> productos = repository.buscarPorNombreContiene("Café");
```

**SQL generado:**
```sql
SELECT * FROM producto_agricola
WHERE LOWER(nombre) LIKE LOWER(CONCAT('%', 'Café', '%'));
```

### 8.2 Consulta con JOIN (Maestro-Detalle)

**Archivo:** `ProductoAgricolaJpaRepository.java:110`

```java
/**
 * CONSULTA MAESTRO-DETALLE: Productos con dos cosechas
 * Esta consulta retorna:
 * - Datos del maestro (ProductoAgricola)
 * - Datos de la primera cosecha
 * - Datos de la segunda cosecha
 */
@Query("SELECT p.id, p.nombre, p.tipoCultivo, " +
       "c.id, c.fechaCosecha, c.cantidadRecolectada, c.calidadProducto, " +
       "c2.id, c2.fechaCosecha, c2.cantidadRecolectada, c2.calidadProducto " +
       "FROM ProductoAgricola p " +
       "JOIN Cosecha c ON c.producto = p " +
       "LEFT JOIN Cosecha c2 ON c2.producto = p AND c2.id != c.id " +
       "WHERE c.calidadProducto = 'Premium' " +
       "ORDER BY p.nombre, c.fechaCosecha DESC")
List<Object[]> findProductosConDosDetallesCosechas();
```

**SQL generado por Hibernate:**
```sql
SELECT
    p.id, p.nombre, p.tipo_cultivo,
    c1.id, c1.fecha_cosecha, c1.cantidad_recolectada, c1.calidad_producto,
    c2.id, c2.fecha_cosecha, c2.cantidad_recolectada, c2.calidad_producto
FROM producto_agricola p
JOIN cosecha c1 ON c1.producto_id = p.id
LEFT JOIN cosecha c2 ON c2.producto_id = p.id AND c2.id != c1.id
WHERE c1.calidad_producto = 'Premium'
ORDER BY p.nombre, c1.fecha_cosecha DESC;
```

**Resultado:**
```
┌─────────┬────────────────────┬──────────┬─────────┬───────────┬────────┬─────────┬───────────┬────────┐
│ Prod ID │ Nombre             │ Cultivo  │ Cos1 ID │ Fecha1    │ Cant1  │ Cos2 ID │ Fecha2    │ Cant2  │
├─────────┼────────────────────┼──────────┼─────────┼───────────┼────────┼─────────┼───────────┼────────┤
│ AGR001  │ Café Premium...    │ Café     │ COS001  │ 2025-04-10│ 2800   │ COS002  │ 2025-05-15│ 3200   │
│ AGR002  │ Arroz Orgánico...  │ Arroz    │ COS004  │ 2025-07-20│ 18000  │ COS003  │ 2025-06-01│ 15000  │
└─────────┴────────────────────┴──────────┴─────────┴───────────┴────────┴─────────┴───────────┴────────┘
```

### 8.3 Consulta con Agregación

**Archivo:** `CosechaJpaRepository.java:75`

```java
@Query("SELECT p.id, p.nombre, COUNT(c), SUM(c.cantidadRecolectada), " +
       "AVG(c.cantidadRecolectada) " +
       "FROM Cosecha c " +
       "JOIN c.producto p " +
       "GROUP BY p.id, p.nombre")
List<Object[]> obtenerEstadisticasCosechasPorProducto();
```

**SQL generado:**
```sql
SELECT
    p.id,
    p.nombre,
    COUNT(c.id) AS total_cosechas,
    SUM(c.cantidad_recolectada) AS total_kg,
    AVG(c.cantidad_recolectada) AS promedio_kg
FROM cosecha c
JOIN producto_agricola p ON c.producto_id = p.id
GROUP BY p.id, p.nombre;
```

**Resultado:**
```
┌─────────┬────────────────────────┬─────────┬──────────┬─────────────┐
│ Prod ID │ Nombre                 │ Total   │ Total Kg │ Promedio Kg │
├─────────┼────────────────────────┼─────────┼──────────┼─────────────┤
│ AGR001  │ Café Premium...        │ 2       │ 6000     │ 3000.0      │
│ AGR002  │ Arroz Orgánico...      │ 2       │ 33000    │ 16500.0     │
│ AGR003  │ Cacao Fino de Aroma    │ 1       │ 2500     │ 2500.0      │
└─────────┴────────────────────────┴─────────┴──────────┴─────────────┘
```

---

## 9. Gestión de Transacciones

### 9.1 ¿Qué es una transacción?

Una transacción agrupa varias operaciones de BD en una unidad atómica:

```java
@Transactional
public void transferirProducto(String productoId, String nuevaFinca) {
    // Operación 1: Actualizar producto
    ProductoAgricola producto = repository.findById(productoId).orElseThrow();
    producto.setCodigoFinca(nuevaFinca);
    repository.save(producto);

    // Operación 2: Registrar en auditoría
    auditService.registrarCambio(productoId, "Cambio de finca");

    // Si algo falla aquí, AMBAS operaciones se revierten (ROLLBACK)
    // Si todo sale bien, AMBAS se confirman (COMMIT)
}
```

### 9.2 Ejemplo en el proyecto

**Archivo:** `CosechaServiceImpl.java:37`

```java
@Service
@Transactional  // ← Toda la clase es transaccional
public class CosechaServiceImpl implements CosechaService {

    @Override
    public Cosecha crearCosecha(Cosecha cosecha) {
        // BEGIN TRANSACTION

        validarCosecha(cosecha);

        // SELECT * FROM producto_agricola WHERE id = ?
        ProductoAgricola producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNotFoundException(...));

        cosecha.setProducto(producto);

        // INSERT INTO cosecha (...)
        Cosecha saved = cosechaRepository.save(cosecha);

        // COMMIT (si todo sale bien)
        // o ROLLBACK (si hay excepción)

        return saved;
    }
}
```

### 9.3 Transacciones de solo lectura

```java
@Override
@Transactional(readOnly = true)  // ← Optimización para SELECT
public List<Cosecha> obtenerCosechasPorProducto(String productoId) {
    // No modifica datos, solo consulta
    return cosechaRepository.findCosechasByProductoId(productoId);
}
```

**Beneficios de `readOnly = true`:**
- ✅ Mejor performance (Hibernate no hace dirty checking)
- ✅ No se crea snapshot de entidades
- ✅ La BD puede optimizar la consulta

---

## 10. Pool de Conexiones (HikariCP)

### 10.1 ¿Qué es un pool de conexiones?

En lugar de abrir/cerrar una conexión a MySQL para cada petición:

```
❌ SIN POOL:
Petición 1: Abrir conexión → Ejecutar query → Cerrar conexión (lento)
Petición 2: Abrir conexión → Ejecutar query → Cerrar conexión (lento)
Petición 3: Abrir conexión → Ejecutar query → Cerrar conexión (lento)

✅ CON POOL (HikariCP):
Petición 1: Tomar conexión del pool → Ejecutar query → Devolver al pool
Petición 2: Tomar conexión del pool → Ejecutar query → Devolver al pool
Petición 3: Tomar conexión del pool → Ejecutar query → Devolver al pool

Las conexiones se reutilizan (mucho más rápido)
```

### 10.2 Configuración en el proyecto

**Archivo:** `application.yml` (configuración implícita)

Spring Boot configura HikariCP automáticamente con valores por defecto:

```yaml
spring:
  datasource:
    hikari:
      # Número mínimo de conexiones en el pool
      minimum-idle: 10

      # Número máximo de conexiones
      maximum-pool-size: 20

      # Tiempo máximo de espera por una conexión (ms)
      connection-timeout: 30000

      # Tiempo máximo que una conexión puede estar inactiva (ms)
      idle-timeout: 600000

      # Tiempo de vida máximo de una conexión (ms)
      max-lifetime: 1800000
```

### 10.3 Log de inicio del pool

En los logs del servidor puedes ver:

```
2025-10-30T15:58:40.578  INFO  HikariDataSource : HikariPool-1 - Starting...
2025-10-30T15:58:40.744  INFO  HikariPool       : HikariPool-1 - Added connection
                                                   com.mysql.cj.jdbc.ConnectionImpl@26a63fa3
2025-10-30T15:58:40.745  INFO  HikariDataSource : HikariPool-1 - Start completed.
```

Esto significa:
- ✅ Pool de conexiones creado con nombre "HikariPool-1"
- ✅ Primera conexión establecida exitosamente
- ✅ Sistema listo para recibir peticiones

---

## 🎓 Resumen para Explicar al Profesor

### Puntos Clave a Mencionar:

1. **Arquitectura en Capas:**
   - Controller → Service → Repository → Database
   - Separación de responsabilidades clara

2. **Mapeo Objeto-Relacional (JPA/Hibernate):**
   - Entidades Java se mapean a tablas MySQL
   - `@Entity`, `@Table`, `@Column` definen el mapeo
   - Hibernate traduce operaciones Java a SQL automáticamente

3. **Relación Maestro-Detalle:**
   - `@ManyToOne` en Cosecha hacia ProductoAgricola
   - Foreign Key con integridad referencial
   - Sin usar listas (cumple requisito del prototipo)

4. **Repositorios JPA:**
   - Spring Data JPA genera implementación automática
   - Métodos por convención de nombres
   - Consultas personalizadas con `@Query`

5. **Gestión de Transacciones:**
   - `@Transactional` garantiza atomicidad
   - Rollback automático en caso de error
   - Optimización con `readOnly = true`

6. **Pool de Conexiones:**
   - HikariCP reutiliza conexiones
   - Mejor performance que crear/destruir conexiones
   - Configuración automática por Spring Boot

7. **Consultas Personalizadas:**
   - 10+ queries implementadas
   - Incluye consulta maestro con 2 detalles (requisito)
   - JPQL se traduce a SQL optimizado

---

## 📁 Archivos Clave del Proyecto

| Archivo | Ubicación | Propósito |
|---------|-----------|-----------|
| `application.yml` | `src/main/resources/` | Configuración de la conexión |
| `ProductoAgricola.java` | `model/` | Entidad maestro con anotaciones JPA |
| `Cosecha.java` | `model/` | Entidad detalle con @ManyToOne |
| `ProductoAgricolaJpaRepository.java` | `repository/` | Repositorio JPA con queries |
| `CosechaJpaRepository.java` | `repository/` | Repositorio JPA con queries |
| `ProductoAgricolaServiceImpl.java` | `service/impl/` | Lógica de negocio transaccional |
| `CosechaServiceImpl.java` | `service/impl/` | Lógica de negocio transaccional |
| `schema.sql` | `database/` | Script de creación de BD |

---

**Universidad de Ibagué**
Facultad de Ingeniería
Desarrollo de Aplicaciones Empresariales
**Tercer Prototipo 2025B**

Este documento proporciona una explicación completa de cómo funciona la conexión y persistencia de datos con MySQL en el proyecto de gestión agropecuaria.
