package co.unibague.agropecuario.rest.service;

import co.unibague.agropecuario.rest.model.Cosecha;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Cosechas
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
public interface CosechaService {

    /**
     * Crea una nueva cosecha
     */
    Cosecha crearCosecha(Cosecha cosecha);

    /**
     * Obtiene una cosecha por su ID
     */
    Optional<Cosecha> obtenerCosechaPorId(String id);

    /**
     * Obtiene todas las cosechas
     */
    List<Cosecha> obtenerTodasLasCosechas();

    /**
     * ⚠️ CRÍTICO - Obtiene todas las cosechas de un producto específico
     * Método clave para la relación maestro-detalle
     */
    List<Cosecha> obtenerCosechasPorProducto(String productoId);

    /**
     * Obtiene cosechas por calidad del producto
     */
    List<Cosecha> obtenerCosechasPorCalidad(String calidad);

    /**
     * Obtiene cosechas por estado
     */
    List<Cosecha> obtenerCosechasPorEstado(String estado);

    /**
     * Obtiene cosechas en un rango de fechas
     */
    List<Cosecha> obtenerCosechasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Actualiza una cosecha existente
     */
    Cosecha actualizarCosecha(String id, Cosecha cosecha);

    /**
     * Elimina una cosecha
     */
    boolean eliminarCosecha(String id);

    /**
     * Verifica si existe una cosecha
     */
    boolean existeCosecha(String id);

    /**
     * Cuenta el total de cosechas
     */
    int contarCosechas();

    /**
     * ⚠️ CRÍTICO - Cuenta las cosechas de un producto
     */
    int contarCosechasPorProducto(String productoId);

    /**
     * Elimina todas las cosechas de un producto
     */
    int eliminarCosechasPorProducto(String productoId);

    /**
     * Calcula estadísticas de cosechas para un producto
     */
    java.util.Map<String, Object> calcularEstadisticasPorProducto(String productoId);
}