-- ============================================================================
-- UNIVERSIDAD DE IBAGUÉ
-- FACULTAD DE INGENIERÍA
-- DESARROLLO DE APLICACIONES EMPRESARIALES
-- TERCER PROTOTIPO 2025B - SCRIPT DE CREACIÓN DE BASE DE DATOS
-- ============================================================================
-- Sistema de Gestión Agropecuaria
-- Relación Maestro-Detalle: ProductoAgricola (1) ---> (0..*) Cosecha
-- ============================================================================

-- ============================================================================
-- 1. CREACIÓN DE BASE DE DATOS Y USUARIO
-- ============================================================================

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS agropecuario_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos creada
USE agropecuario_db;

-- Crear usuario para la aplicación (ejecutar con privilegios de administrador)
-- NOTA: Cambiar la contraseña en producción
CREATE USER IF NOT EXISTS 'agropecuario_user'@'localhost'
    IDENTIFIED BY 'agropecuario_password';

-- Otorgar permisos al usuario
GRANT ALL PRIVILEGES ON agropecuario_db.* TO 'agropecuario_user'@'localhost';
FLUSH PRIVILEGES;

-- ============================================================================
-- 2. TABLA MAESTRO: producto_agricola
-- ============================================================================

DROP TABLE IF EXISTS cosecha;
DROP TABLE IF EXISTS producto_agricola;

CREATE TABLE producto_agricola (
    -- Llave primaria
    id VARCHAR(10) NOT NULL PRIMARY KEY COMMENT 'Identificador único del producto (ej: AGR001)',

    -- Atributos obligatorios
    nombre VARCHAR(100) NOT NULL COMMENT 'Nombre del producto agrícola',
    hectareas_cultivadas DOUBLE NOT NULL COMMENT 'Hectáreas cultivadas (debe ser > 0.1)',
    cantidad_producida INT NOT NULL COMMENT 'Cantidad producida en kg',
    tipo_cultivo VARCHAR(50) NOT NULL COMMENT 'Tipo de cultivo (ej: Café, Arroz, Cacao)',
    precio_venta DOUBLE NOT NULL COMMENT 'Precio de venta por unidad',
    costo_produccion DOUBLE NOT NULL COMMENT 'Costo de producción total',

    -- Atributos opcionales
    fecha_produccion DATETIME COMMENT 'Fecha y hora de producción',
    rendimiento_por_ha DOUBLE COMMENT 'Rendimiento por hectárea',
    temporada VARCHAR(50) DEFAULT 'Todo el año' COMMENT 'Temporada de cultivo',
    tipo_suelo VARCHAR(50) DEFAULT 'Franco' COMMENT 'Tipo de suelo utilizado',
    codigo_finca VARCHAR(20) COMMENT 'Código de la finca',

    -- Constraints
    CONSTRAINT chk_hectareas CHECK (hectareas_cultivadas >= 0.1 AND hectareas_cultivadas <= 10000),
    CONSTRAINT chk_cantidad CHECK (cantidad_producida >= 1 AND cantidad_producida <= 1000000),
    CONSTRAINT chk_precio CHECK (precio_venta >= 100 AND precio_venta <= 1000000),
    CONSTRAINT chk_costo CHECK (costo_produccion >= 100),

    -- Índices para mejorar el rendimiento de búsquedas
    INDEX idx_tipo_cultivo (tipo_cultivo),
    INDEX idx_nombre (nombre),
    INDEX idx_fecha_produccion (fecha_produccion),
    INDEX idx_codigo_finca (codigo_finca)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Tabla maestro - Productos agrícolas';

-- ============================================================================
-- 3. TABLA DETALLE: cosecha
-- ============================================================================

CREATE TABLE cosecha (
    -- Llave primaria
    id VARCHAR(10) NOT NULL PRIMARY KEY COMMENT 'Identificador único de la cosecha (ej: COS001)',

    -- Llave foránea (relación Many-to-One con producto_agricola)
    producto_id VARCHAR(10) NOT NULL COMMENT 'Referencia al producto agrícola',

    -- Atributos obligatorios
    fecha_cosecha DATETIME NOT NULL COMMENT 'Fecha y hora de la cosecha',
    cantidad_recolectada INT NOT NULL COMMENT 'Cantidad recolectada en kg',
    calidad_producto VARCHAR(30) NOT NULL COMMENT 'Calidad del producto: Premium, Extra, Estándar, Segunda',

    -- Atributos opcionales
    numero_trabajadores INT COMMENT 'Número de trabajadores en la cosecha',
    costo_mano_obra DOUBLE DEFAULT 0.0 COMMENT 'Costo de mano de obra',
    condiciones_climaticas VARCHAR(50) DEFAULT 'Normal' COMMENT 'Condiciones climáticas durante la cosecha',
    estado_cosecha VARCHAR(30) DEFAULT 'Pendiente' COMMENT 'Estado: Completada, En proceso, Pendiente',
    observaciones VARCHAR(500) COMMENT 'Observaciones adicionales',

    -- Constraints
    CONSTRAINT chk_cantidad_recolectada CHECK (cantidad_recolectada >= 1),
    CONSTRAINT chk_numero_trabajadores CHECK (numero_trabajadores IS NULL OR numero_trabajadores >= 1),
    CONSTRAINT chk_costo_mano_obra CHECK (costo_mano_obra IS NULL OR costo_mano_obra >= 0),
    CONSTRAINT chk_calidad CHECK (calidad_producto IN ('Premium', 'Extra', 'Estándar', 'Segunda')),
    CONSTRAINT chk_estado CHECK (estado_cosecha IN ('Completada', 'En proceso', 'Pendiente')),

    -- Foreign Key constraint
    CONSTRAINT fk_cosecha_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto_agricola(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Índices para mejorar el rendimiento
    INDEX idx_producto_id (producto_id),
    INDEX idx_fecha_cosecha (fecha_cosecha),
    INDEX idx_calidad (calidad_producto),
    INDEX idx_estado (estado_cosecha)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Tabla detalle - Cosechas de productos agrícolas';

-- ============================================================================
-- 4. DATOS INICIALES DE PRUEBA
-- ============================================================================

-- Insertar productos agrícolas de prueba
INSERT INTO producto_agricola (
    id, nombre, hectareas_cultivadas, cantidad_producida, fecha_produccion,
    tipo_cultivo, precio_venta, costo_produccion, rendimiento_por_ha,
    temporada, tipo_suelo, codigo_finca
) VALUES
-- Producto 1: Café Premium
('AGR001', 'Café Premium Colombiano', 50.5, 12000,
 '2025-01-15 08:30:00', 'Café', 8500.00, 5200.00, 237.62,
 'Todo el año', 'Franco arcilloso', 'FNC-001'),

-- Producto 2: Arroz Orgánico
('AGR002', 'Arroz Orgánico de Primera', 120.0, 45000,
 '2025-02-10 10:00:00', 'Arroz', 3200.00, 2100.00, 375.00,
 'Lluviosa', 'Franco limoso', 'ARR-002'),

-- Producto 3: Cacao Fino
('AGR003', 'Cacao Fino de Aroma', 35.8, 8500,
 '2025-03-05 09:15:00', 'Cacao', 12000.00, 7500.00, 237.43,
 'Todo el año', 'Franco', 'CAC-003');

-- Insertar cosechas de prueba
INSERT INTO cosecha (
    id, producto_id, fecha_cosecha, cantidad_recolectada, calidad_producto,
    numero_trabajadores, costo_mano_obra, condiciones_climaticas,
    estado_cosecha, observaciones
) VALUES
-- Cosechas para Café Premium (AGR001)
('COS001', 'AGR001', '2025-04-10 07:00:00', 2800, 'Premium',
 15, 450000.00, 'Soleado', 'Completada',
 'Excelente calidad, granos grandes y uniformes'),

('COS002', 'AGR001', '2025-05-15 06:30:00', 3200, 'Extra',
 18, 540000.00, 'Parcialmente nublado', 'Completada',
 'Buena producción, algunos granos de menor tamaño'),

-- Cosechas para Arroz Orgánico (AGR002)
('COS003', 'AGR002', '2025-06-01 08:00:00', 15000, 'Estándar',
 25, 750000.00, 'Lluvioso', 'Completada',
 'Cosecha en temporada de lluvias, rendimiento esperado'),

('COS004', 'AGR002', '2025-07-20 09:00:00', 18000, 'Premium',
 30, 900000.00, 'Soleado', 'Completada',
 'Excelente calidad orgánica certificada'),

-- Cosecha para Cacao Fino (AGR003)
('COS005', 'AGR003', '2025-08-12 10:00:00', 2500, 'Premium',
 12, 360000.00, 'Soleado', 'Completada',
 'Cacao de aroma excepcional, fermentación perfecta');

-- ============================================================================
-- 5. CONSULTAS DE VERIFICACIÓN
-- ============================================================================

-- Ver todos los productos
SELECT * FROM producto_agricola ORDER BY id;

-- Ver todas las cosechas
SELECT * FROM cosecha ORDER BY id;

-- Consulta maestro-detalle: Productos con sus cosechas
SELECT
    p.id AS producto_id,
    p.nombre AS producto_nombre,
    p.tipo_cultivo,
    c.id AS cosecha_id,
    c.fecha_cosecha,
    c.cantidad_recolectada,
    c.calidad_producto
FROM producto_agricola p
LEFT JOIN cosecha c ON p.id = c.producto_id
ORDER BY p.id, c.fecha_cosecha DESC;

-- Estadísticas por producto
SELECT
    p.id,
    p.nombre,
    COUNT(c.id) AS total_cosechas,
    COALESCE(SUM(c.cantidad_recolectada), 0) AS total_kg_cosechados,
    COALESCE(AVG(c.cantidad_recolectada), 0) AS promedio_kg_por_cosecha
FROM producto_agricola p
LEFT JOIN cosecha c ON p.id = c.producto_id
GROUP BY p.id, p.nombre
ORDER BY total_kg_cosechados DESC;

-- Cosechas de calidad Premium
SELECT
    c.id,
    c.fecha_cosecha,
    p.nombre AS producto,
    c.cantidad_recolectada,
    c.calidad_producto
FROM cosecha c
JOIN producto_agricola p ON c.producto_id = p.id
WHERE c.calidad_producto = 'Premium'
ORDER BY c.fecha_cosecha DESC;

-- ============================================================================
-- 6. INFORMACIÓN ÚTIL
-- ============================================================================

-- Para conectar desde la aplicación Spring Boot, usar:
-- URL: jdbc:mysql://localhost:3306/agropecuario_db
-- Usuario: agropecuario_user
-- Contraseña: agropecuario_password

-- Para ver el esquema de una tabla:
-- DESCRIBE producto_agricola;
-- DESCRIBE cosecha;

-- Para ver las relaciones:
-- SHOW CREATE TABLE cosecha;

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================
