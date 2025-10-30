package co.unibague.agropecuario.rest.repository;

import co.unibague.agropecuario.rest.model.ProductoAgricola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para ProductoAgricola
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 * Tercer Prototipo - Persistencia con MySQL
 */
@Repository
public interface ProductoAgricolaJpaRepository extends JpaRepository<ProductoAgricola, String> {

    // ========== CONSULTAS BÁSICAS AUTOMÁTICAS ==========

    /**
     * Buscar por nombre (query method automático)
     */
    Optional<ProductoAgricola> findByNombre(String nombre);

    /**
     * Buscar por tipo de cultivo (query method automático)
     */
    List<ProductoAgricola> findByTipoCultivo(String tipoCultivo);

    /**
     * Buscar por rango de hectáreas (query method automático)
     */
    List<ProductoAgricola> findByHectareasCultivadasBetween(Double min, Double max);

    /**
     * Verificar si existe un producto con ese nombre
     */
    boolean existsByNombre(String nombre);

    // ========== CONSULTAS PERSONALIZADAS JPA ==========

    /**
     * CONSULTA PERSONALIZADA 1: Buscar productos por tipo de cultivo y calidad
     * Esta consulta busca productos que tienen al menos una cosecha de calidad específica
     */
    @Query("SELECT DISTINCT p FROM ProductoAgricola p " +
           "JOIN Cosecha c ON c.producto = p " +
           "WHERE p.tipoCultivo = :tipoCultivo " +
           "AND c.calidadProducto = :calidad")
    List<ProductoAgricola> findProductosByTipoCultivoAndCalidadCosecha(
            @Param("tipoCultivo") String tipoCultivo,
            @Param("calidad") String calidad);

    /**
     * CONSULTA PERSONALIZADA 2: Productos con rentabilidad superior a un umbral
     * Calcula la rentabilidad: (ingresoTotal - costoTotal) / hectáreas
     */
    @Query("SELECT p FROM ProductoAgricola p " +
           "WHERE (p.cantidadProducida * p.precioVenta - p.costoProduccion * p.hectareasCultivadas) / p.hectareasCultivadas > :rentabilidadMinima")
    List<ProductoAgricola> findProductosConRentabilidadSuperiorA(
            @Param("rentabilidadMinima") Double rentabilidadMinima);

    /**
     * CONSULTA PERSONALIZADA 3: Productos más productivos por hectárea
     * Ordena por cantidad producida / hectáreas cultivadas
     */
    @Query("SELECT p FROM ProductoAgricola p " +
           "ORDER BY (p.cantidadProducida / p.hectareasCultivadas) DESC")
    List<ProductoAgricola> findProductosOrdenadosPorProductividadPorHectarea();

    /**
     * CONSULTA PERSONALIZADA 4: Productos con margen de ganancia alto
     */
    @Query("SELECT p FROM ProductoAgricola p " +
           "WHERE ((p.precioVenta - p.costoProduccion) / p.costoProduccion) * 100 > :margenMinimo")
    List<ProductoAgricola> findProductosConMargenGananciaAlto(
            @Param("margenMinimo") Double margenMinimo);

    /**
     * CONSULTA PERSONALIZADA 5: Productos de una finca específica ordenados por precio
     */
    @Query("SELECT p FROM ProductoAgricola p " +
           "WHERE p.codigoFinca = :codigoFinca " +
           "ORDER BY p.precioVenta DESC")
    List<ProductoAgricola> findProductosByFincaOrdenadosPorPrecio(
            @Param("codigoFinca") String codigoFinca);

    /**
     * CONSULTA PERSONALIZADA 6: Estadísticas de producción por tipo de cultivo
     * IMPORTANTE: Esta consulta devuelve datos de la tabla maestro con agregaciones
     */
    @Query("SELECT p.tipoCultivo, " +
           "COUNT(p), " +
           "SUM(p.hectareasCultivadas), " +
           "SUM(p.cantidadProducida), " +
           "AVG(p.precioVenta) " +
           "FROM ProductoAgricola p " +
           "GROUP BY p.tipoCultivo")
    List<Object[]> obtenerEstadisticasPorTipoCultivo();

    /**
     * CONSULTA PERSONALIZADA 7: Productos con múltiples cosechas
     * Cuenta cuántas cosechas tiene cada producto
     */
    @Query("SELECT p, COUNT(c) FROM ProductoAgricola p " +
           "LEFT JOIN Cosecha c ON c.producto = p " +
           "GROUP BY p " +
           "HAVING COUNT(c) > :minimoCoechas")
    List<Object[]> findProductosConMultiplesCosechas(
            @Param("minimoCoechas") Long minimoCoechas);

    /**
     * CONSULTA PERSONALIZADA 8 (Maestro-Detalle):
     * Productos con sus cosechas de calidad premium
     * Retorna datos del maestro (ProductoAgricola) junto con datos de dos detalles (Cosecha)
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

    /**
     * CONSULTA PERSONALIZADA 9: Buscar por nombre (like, insensible a mayúsculas)
     */
    @Query("SELECT p FROM ProductoAgricola p " +
           "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<ProductoAgricola> buscarPorNombreContiene(@Param("nombre") String nombre);

    /**
     * CONSULTA PERSONALIZADA 10: Productos ordenados por ingreso total
     */
    @Query("SELECT p FROM ProductoAgricola p " +
           "ORDER BY (p.cantidadProducida * p.precioVenta) DESC")
    List<ProductoAgricola> findProductosOrdenadosPorIngresoTotal();
}
