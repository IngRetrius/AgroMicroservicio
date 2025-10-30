# Configuración de Base de Datos MySQL

## Universidad de Ibagué - Tercer Prototipo de Microservicios

Este documento explica cómo configurar MySQL para el proyecto de gestión agropecuaria.

---

## 📋 Requisitos Previos

1. **MySQL Server 8.0+** instalado
2. **MySQL Workbench** (recomendado para ejecutar scripts)
3. Java 21
4. Maven 3.8+

---

## 🔧 Instalación de MySQL (si no está instalado)

### Windows:
1. Descargar MySQL Installer desde: https://dev.mysql.com/downloads/installer/
2. Ejecutar el instalador y seleccionar "Developer Default"
3. Configurar la contraseña del usuario root durante la instalación
4. Instalar MySQL Workbench (incluido en el instalador)

### Verificar instalación:
```bash
mysql --version
```

---

## 🗄️ Configuración de la Base de Datos

### Opción 1: Usando MySQL Workbench (RECOMENDADO)

1. **Abrir MySQL Workbench**

2. **Conectarse al servidor MySQL**
   - Click en la conexión "Local instance MySQL80"
   - Ingresar la contraseña del usuario root

3. **Ejecutar el script de creación**
   - Abrir el archivo: `database/schema.sql`
   - Click en el ícono del rayo ⚡ (Execute) o presionar `Ctrl + Shift + Enter`
   - Verificar que todas las sentencias se ejecutaron correctamente

4. **Verificar la creación**
   ```sql
   -- En MySQL Workbench, ejecutar:
   USE agropecuario_db;
   SHOW TABLES;
   SELECT * FROM producto_agricola;
   SELECT * FROM cosecha;
   ```

### Opción 2: Usando la línea de comandos

```bash
# 1. Conectarse a MySQL
mysql -u root -p

# 2. Ejecutar el script
source C:/Users/USUARIO1/IdeaProjects/agropecuario-rest-server/database/schema.sql

# 3. Verificar
USE agropecuario_db;
SHOW TABLES;
```

---

## 📊 Estructura de la Base de Datos

### Tabla Maestro: `producto_agricola`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | VARCHAR(10) PK | ID único (AGR001, AGR002, ...) |
| nombre | VARCHAR(100) | Nombre del producto |
| hectareas_cultivadas | DOUBLE | Hectáreas cultivadas |
| cantidad_producida | INT | Cantidad producida en kg |
| tipo_cultivo | VARCHAR(50) | Tipo de cultivo |
| precio_venta | DOUBLE | Precio de venta |
| costo_produccion | DOUBLE | Costo de producción |
| fecha_produccion | DATETIME | Fecha de producción |
| ... | ... | Otros campos opcionales |

### Tabla Detalle: `cosecha`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | VARCHAR(10) PK | ID único (COS001, COS002, ...) |
| producto_id | VARCHAR(10) FK | Referencia a producto_agricola |
| fecha_cosecha | DATETIME | Fecha de la cosecha |
| cantidad_recolectada | INT | Cantidad en kg |
| calidad_producto | VARCHAR(30) | Calidad (Premium, Extra, etc.) |
| numero_trabajadores | INT | Número de trabajadores |
| costo_mano_obra | DOUBLE | Costo de mano de obra |
| ... | ... | Otros campos opcionales |

### Relación:
- **Maestro-Detalle**: Un ProductoAgricola puede tener muchas Cosechas (1:N)
- **Foreign Key**: `cosecha.producto_id` → `producto_agricola.id`
- **Cascade**: ON DELETE CASCADE, ON UPDATE CASCADE

---

## 🔐 Credenciales de la Base de Datos

**Base de datos:**
- Nombre: `agropecuario_db`
- Charset: `utf8mb4`
- Collation: `utf8mb4_unicode_ci`

**Usuario de aplicación:**
- Usuario: `agropecuario_user`
- Contraseña: `agropecuario_password`
- Host: `localhost`
- Permisos: Todos en `agropecuario_db.*`

**Conexión JDBC:**
```
jdbc:mysql://localhost:3306/agropecuario_db?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true
```

---

## 📝 Datos de Prueba

El script `schema.sql` incluye datos de prueba:

**3 Productos Agrícolas:**
1. AGR001 - Café Premium Colombiano
2. AGR002 - Arroz Orgánico de Primera
3. AGR003 - Cacao Fino de Aroma

**5 Cosechas:**
1. COS001 - Cosecha de Café (Premium)
2. COS002 - Cosecha de Café (Extra)
3. COS003 - Cosecha de Arroz (Estándar)
4. COS004 - Cosecha de Arroz (Premium)
5. COS005 - Cosecha de Cacao (Premium)

---

## 🚀 Ejecutar la Aplicación

### 1. Verificar que MySQL está corriendo

```bash
# Windows (en PowerShell como administrador)
Get-Service MySQL80

# Si no está corriendo, iniciarlo:
Start-Service MySQL80
```

### 2. Actualizar credenciales (si es necesario)

Si cambió las credenciales, editar `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agropecuario_db?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true
    username: agropecuario_user
    password: TU_NUEVA_CONTRASEÑA
```

### 3. Compilar y ejecutar

```bash
# Limpiar y compilar
mvn clean package

# Ejecutar la aplicación
mvn spring-boot:run

# O ejecutar el JAR directamente
java -jar target/agropecuario-rest-server-1.0.0.jar
```

### 4. Verificar que la aplicación conectó exitosamente

Buscar en los logs:
```
✓ 3 productos agrícolas creados
✓ 5 cosechas creadas
=== DATOS INICIALES CARGADOS EXITOSAMENTE ===
```

**NOTA:** Los datos iniciales solo se cargan la primera vez. Si la BD ya tiene datos, se omite la carga.

---

## 🧪 Probar los Endpoints

### Verificar que el servidor está corriendo:
```bash
curl http://localhost:8081/actuator/health
```

### Obtener todos los productos:
```bash
curl http://localhost:8081/api/productos
```

### Obtener cosechas de un producto:
```bash
curl http://localhost:8081/api/productos/AGR001/cosechas
```

---

## 🔍 Consultas Útiles en MySQL

### Ver productos con sus cosechas:
```sql
SELECT
    p.id AS producto_id,
    p.nombre AS producto,
    c.id AS cosecha_id,
    c.fecha_cosecha,
    c.cantidad_recolectada,
    c.calidad_producto
FROM producto_agricola p
LEFT JOIN cosecha c ON p.id = c.producto_id
ORDER BY p.id, c.fecha_cosecha DESC;
```

### Estadísticas por producto:
```sql
SELECT
    p.nombre,
    COUNT(c.id) AS total_cosechas,
    SUM(c.cantidad_recolectada) AS total_kg,
    AVG(c.cantidad_recolectada) AS promedio_kg
FROM producto_agricola p
LEFT JOIN cosecha c ON p.id = c.producto_id
GROUP BY p.id, p.nombre;
```

### Cosechas de calidad Premium:
```sql
SELECT
    c.id,
    p.nombre AS producto,
    c.cantidad_recolectada,
    c.calidad_producto
FROM cosecha c
JOIN producto_agricola p ON c.producto_id = p.id
WHERE c.calidad_producto = 'Premium';
```

---

## ⚠️ Solución de Problemas

### Error: "Access denied for user 'agropecuario_user'@'localhost'"

**Solución:**
```sql
-- Conectarse como root
mysql -u root -p

-- Recrear el usuario
DROP USER IF EXISTS 'agropecuario_user'@'localhost';
CREATE USER 'agropecuario_user'@'localhost' IDENTIFIED BY 'agropecuario_password';
GRANT ALL PRIVILEGES ON agropecuario_db.* TO 'agropecuario_user'@'localhost';
FLUSH PRIVILEGES;
```

### Error: "Unknown database 'agropecuario_db'"

**Solución:** Ejecutar nuevamente el script `schema.sql`

### Error: "Communications link failure"

**Solución:**
1. Verificar que MySQL está corriendo
2. Verificar el puerto (por defecto 3306)
3. Verificar el firewall

### Error: "Table already exists"

**Solución:** El script tiene `DROP TABLE IF EXISTS`. Si hay problemas, ejecutar manualmente:
```sql
USE agropecuario_db;
DROP TABLE IF EXISTS cosecha;
DROP TABLE IF EXISTS producto_agricola;
```
Luego ejecutar el script nuevamente.

---

## 📚 Tecnologías Utilizadas

- **MySQL 8.0+** - Sistema de gestión de base de datos
- **Hibernate/JPA** - ORM para mapeo objeto-relacional
- **Spring Data JPA** - Capa de abstracción para acceso a datos
- **HikariCP** - Pool de conexiones (incluido en Spring Boot)

---

## 📖 Documentación Adicional

- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Hibernate ORM](https://hibernate.org/orm/)

---

## 👥 Autor

**Universidad de Ibagué**
Facultad de Ingeniería
Desarrollo de Aplicaciones Empresariales
Tercer Prototipo 2025B

---

## 📄 Licencia

Proyecto académico - Universidad de Ibagué
