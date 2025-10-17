package co.unibague.agropecuario.rest.repository;

import co.unibague.agropecuario.rest.model.Cosecha;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de Cosechas
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
public interface CosechaRepository {

    /**
     * Guarda una nueva cosecha
     */
    Cosecha save(Cosecha cosecha);

    /**
     * Busca una cosecha por su ID
     */
    Optional<Cosecha> findById(String id);

    /**
     * Obtiene todas las cosechas
     */
    List<Cosecha> findAll();

    /**
     * ⚠️ CRÍTICO - Busca todas las cosechas de un producto específico
     * Método clave para la relación maestro-detalle
     */
    List<Cosecha> findByProductoId(String productoId);

    /**
     * Busca cosechas por calidad del producto
     */
    List<Cosecha> findByCalidad(String calidad);

    /**
     * Busca cosechas por estado
     */
    List<Cosecha> findByEstado(String estado);

    /**
     * Busca cosechas por rango de fechas
     */
    List<Cosecha> findByFechaRange(java.time.LocalDateTime inicio,
                                   java.time.LocalDateTime fin);

    /**
     * Actualiza una cosecha existente
     */
    Cosecha update(Cosecha cosecha);

    /**
     * Elimina una cosecha por su ID
     */
    boolean deleteById(String id);

    /**
     * Verifica si existe una cosecha con el ID dado
     */
    boolean existsById(String id);

    /**
     * Cuenta el número total de cosechas
     */
    int count();

    /**
     * ⚠️ CRÍTICO - Cuenta las cosechas de un producto específico
     */
    int countByProductoId(String productoId);

    /**
     * Genera el siguiente ID disponible
     */
    String generateNextId();

    /**
     * Elimina todas las cosechas de un producto
     * (útil cuando se elimina un producto maestro)
     */
    int deleteByProductoId(String productoId);
}