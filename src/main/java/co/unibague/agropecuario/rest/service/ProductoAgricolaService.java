package co.unibague.agropecuario.rest.service;

import co.unibague.agropecuario.rest.model.ProductoAgricola;
import java.util.List;
import java.util.Optional;

public interface ProductoAgricolaService {

    ProductoAgricola crearProducto(ProductoAgricola producto);
    Optional<ProductoAgricola> obtenerProductoPorId(String id);
    List<ProductoAgricola> obtenerTodosLosProductos();
    List<ProductoAgricola> buscarPorTipoCultivo(String tipo);
    List<ProductoAgricola> buscarPorNombre(String nombre);
    List<ProductoAgricola> buscarPorTemporada(String temporada);
    List<ProductoAgricola> buscarPorRangoHectareas(Double minHectareas, Double maxHectareas);
    ProductoAgricola actualizarProducto(String id, ProductoAgricola producto);
    boolean eliminarProducto(String id);
    boolean existeProducto(String id);
    int contarProductos();
}