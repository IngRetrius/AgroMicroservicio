package co.unibague.agropecuario.rest.repository;

import co.unibague.agropecuario.rest.model.ProductoAgricola;
import java.util.List;
import java.util.Optional;

public interface ProductoAgricolaRepository {

    ProductoAgricola save(ProductoAgricola producto);
    Optional<ProductoAgricola> findById(String id);
    List<ProductoAgricola> findAll();
    List<ProductoAgricola> findByTipoCultivo(String tipo);
    List<ProductoAgricola> findByNombreContaining(String nombre);
    List<ProductoAgricola> findByTemporada(String temporada);
    List<ProductoAgricola> findByHectareasRange(Double minHectareas, Double maxHectareas);
    ProductoAgricola update(ProductoAgricola producto);
    boolean deleteById(String id);
    boolean existsById(String id);
    int count();
    String generateNextId();
}