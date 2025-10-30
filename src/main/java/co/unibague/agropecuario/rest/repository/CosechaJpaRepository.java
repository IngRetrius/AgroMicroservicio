package co.unibague.agropecuario.rest.repository;

import co.unibague.agropecuario.rest.model.Cosecha;
import co.unibague.agropecuario.rest.model.ProductoAgricola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para Cosecha
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 * Tercer Prototipo - Persistencia con MySQL
 */
@Repository
public interface CosechaJpaRepository extends JpaRepository<Cosecha, String> {

    // ========== CONSULTAS BÁSICAS AUTOMÁTICAS ==========

    /**
     * Buscar cosechas por producto (usando la entidad ProductoAgricola)
     */
    List<Cosecha> findByProducto(ProductoAgricola producto);

    /**
     * Buscar cosechas por calidad del producto
     */
    List<Cosecha> findByCalidadProducto(String calidadProducto);

    /**
     * Buscar cosechas por estado
     */
    List<Cosecha> findByEstadoCosecha(String estadoCosecha);

    /**
     * Buscar cosechas en un rango de fechas
     */
    List<Cosecha> findByFechaCosechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Contar cosechas por producto
     */
    Long countByProducto(ProductoAgricola producto);

    // ========== CONSULTAS PERSONALIZADAS JPA ==========

    /**
     * CONSULTA PERSONALIZADA 1: Cosechas por ID de producto (usando el join)
     */
    @Query("SELECT c FROM Cosecha c WHERE c.producto.id = :productoId")
    List<Cosecha> findCosechasByProductoId(@Param("productoId") String productoId);

    /**
     * CONSULTA PERSONALIZADA 2: Cosechas de calidad premium ordenadas por cantidad
     */
    @Query("SELECT c FROM Cosecha c " +
           "WHERE c.calidadProducto = 'Premium' " +
           "ORDER BY c.cantidadRecolectada DESC")
    List<Cosecha> findCosechasPremiumOrdenadas();

    /**
     * CONSULTA PERSONALIZADA 3: Cosechas con mejor rendimiento
     * Calcula kg por trabajador
     */
    @Query("SELECT c FROM Cosecha c " +
           "WHERE c.numeroTrabajadores IS NOT NULL AND c.numeroTrabajadores > 0 " +
           "ORDER BY (c.cantidadRecolectada / c.numeroTrabajadores) DESC")
    List<Cosecha> findCosechasConMejorRendimiento();

    /**
     * CONSULTA PERSONALIZADA 4: Cosechas con costo bajo por kg
     */
    @Query("SELECT c FROM Cosecha c " +
           "WHERE c.costoManoObra IS NOT NULL " +
           "AND c.cantidadRecolectada > 0 " +
           "AND (c.costoManoObra / c.cantidadRecolectada) < :costoMaximoPorKg " +
           "ORDER BY (c.costoManoObra / c.cantidadRecolectada) ASC")
    List<Cosecha> findCosechasConCostoBajoPorKg(@Param("costoMaximoPorKg") Double costoMaximoPorKg);

    /**
     * CONSULTA PERSONALIZADA 5: Cosechas recientes (últimos N días)
     */
    @Query("SELECT c FROM Cosecha c " +
           "WHERE c.fechaCosecha >= :fechaLimite " +
           "ORDER BY c.fechaCosecha DESC")
    List<Cosecha> findCosechasRecientes(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * CONSULTA PERSONALIZADA 6: Cosechas por tipo de cultivo del producto
     */
    @Query("SELECT c FROM Cosecha c " +
           "JOIN c.producto p " +
           "WHERE p.tipoCultivo = :tipoCultivo")
    List<Cosecha> findCosechasByTipoCultivo(@Param("tipoCultivo") String tipoCultivo);

    /**
     * CONSULTA PERSONALIZADA 7: Estadísticas de cosechas por producto
     * Devuelve: productoId, nombreProducto, totalCosechas, totalKg, promedioKg
     */
    @Query("SELECT p.id, p.nombre, COUNT(c), SUM(c.cantidadRecolectada), AVG(c.cantidadRecolectada) " +
           "FROM Cosecha c " +
           "JOIN c.producto p " +
           "GROUP BY p.id, p.nombre")
    List<Object[]> obtenerEstadisticasCosechasPorProducto();

    /**
     * CONSULTA PERSONALIZADA 8: Cosechas por condiciones climáticas
     */
    @Query("SELECT c FROM Cosecha c " +
           "WHERE c.condicionesClimaticas = :condicion " +
           "ORDER BY c.fechaCosecha DESC")
    List<Cosecha> findCosechasByCondicionClimatica(@Param("condicion") String condicion);

    /**
     * CONSULTA PERSONALIZADA 9: Cosechas completadas de un producto específico
     */
    @Query("SELECT c FROM Cosecha c " +
           "WHERE c.producto.id = :productoId " +
           "AND c.estadoCosecha = 'Completada' " +
           "ORDER BY c.fechaCosecha DESC")
    List<Cosecha> findCosechasCompletadasByProductoId(@Param("productoId") String productoId);

    /**
     * CONSULTA PERSONALIZADA 10: Cosechas con cantidad superior a un umbral
     */
    @Query("SELECT c FROM Cosecha c " +
           "WHERE c.cantidadRecolectada > :cantidadMinima " +
           "ORDER BY c.cantidadRecolectada DESC")
    List<Cosecha> findCosechasConCantidadSuperiorA(@Param("cantidadMinima") Integer cantidadMinima);

    /**
     * CONSULTA PERSONALIZADA 11: Promedio de cosecha por calidad
     */
    @Query("SELECT c.calidadProducto, AVG(c.cantidadRecolectada), COUNT(c) " +
           "FROM Cosecha c " +
           "GROUP BY c.calidadProducto " +
           "ORDER BY AVG(c.cantidadRecolectada) DESC")
    List<Object[]> obtenerPromediosPorCalidad();

    /**
     * CONSULTA PERSONALIZADA 12: Total de kg cosechados por producto
     */
    @Query("SELECT p.nombre, SUM(c.cantidadRecolectada) " +
           "FROM Cosecha c " +
           "JOIN c.producto p " +
           "GROUP BY p.id, p.nombre " +
           "ORDER BY SUM(c.cantidadRecolectada) DESC")
    List<Object[]> obtenerTotalKgPorProducto();

    /**
     * CONSULTA PERSONALIZADA 13 (Maestro-Detalle):
     * Obtiene información del producto maestro con dos de sus cosechas (detalles)
     */
    @Query("SELECT p.id, p.nombre, p.tipoCultivo, p.hectareasCultivadas, " +
           "c1.id, c1.fechaCosecha, c1.cantidadRecolectada, c1.calidadProducto, " +
           "c2.id, c2.fechaCosecha, c2.cantidadRecolectada, c2.calidadProducto " +
           "FROM ProductoAgricola p " +
           "JOIN Cosecha c1 ON c1.producto = p " +
           "LEFT JOIN Cosecha c2 ON c2.producto = p AND c2.id > c1.id " +
           "WHERE p.id = :productoId " +
           "ORDER BY c1.fechaCosecha DESC, c2.fechaCosecha DESC")
    List<Object[]> findProductoConDosCosechas(@Param("productoId") String productoId);
}
