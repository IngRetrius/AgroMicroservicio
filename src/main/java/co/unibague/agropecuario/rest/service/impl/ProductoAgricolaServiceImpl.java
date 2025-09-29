package co.unibague.agropecuario.rest.service.impl;

import co.unibague.agropecuario.rest.exception.ProductoAlreadyExistsException;
import co.unibague.agropecuario.rest.exception.ProductoNotFoundException;
import co.unibague.agropecuario.rest.model.ProductoAgricola;
import co.unibague.agropecuario.rest.repository.ProductoAgricolaRepository;
import co.unibague.agropecuario.rest.service.ProductoAgricolaService;
import co.unibague.agropecuario.rest.utils.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoAgricolaServiceImpl implements ProductoAgricolaService {

    @Autowired
    private ProductoAgricolaRepository repository;

    @Override
    public ProductoAgricola crearProducto(ProductoAgricola producto) {
        // Validar datos del producto
        validarProducto(producto);

        // Si no tiene ID o está vacío, generar uno nuevo
        if (producto.getId() == null || producto.getId().trim().isEmpty()) {
            producto.setId(repository.generateNextId());
        } else {
            // Si tiene ID, verificar que no exista
            if (repository.existsById(producto.getId())) {
                throw new ProductoAlreadyExistsException("Ya existe un producto con el ID: " + producto.getId());
            }
        }

        return repository.save(producto);
    }

    @Override
    public Optional<ProductoAgricola> obtenerProductoPorId(String id) {
        return repository.findById(id);
    }

    @Override
    public List<ProductoAgricola> obtenerTodosLosProductos() {
        return repository.findAll();
    }

    @Override
    public List<ProductoAgricola> buscarPorTipoCultivo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de cultivo no puede estar vacío");
        }
        return repository.findByTipoCultivo(tipo);
    }

    @Override
    public List<ProductoAgricola> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        return repository.findByNombreContaining(nombre);
    }

    @Override
    public List<ProductoAgricola> buscarPorTemporada(String temporada) {
        if (temporada == null || temporada.trim().isEmpty()) {
            throw new IllegalArgumentException("La temporada no puede estar vacía");
        }
        return repository.findByTemporada(temporada);
    }

    @Override
    public List<ProductoAgricola> buscarPorRangoHectareas(Double minHectareas, Double maxHectareas) {
        if (minHectareas != null && maxHectareas != null && minHectareas > maxHectareas) {
            throw new IllegalArgumentException("El mínimo de hectáreas no puede ser mayor al máximo");
        }
        return repository.findByHectareasRange(minHectareas, maxHectareas);
    }

    @Override
    public ProductoAgricola actualizarProducto(String id, ProductoAgricola producto) {
        if (!repository.existsById(id)) {
            throw new ProductoNotFoundException("Producto no encontrado con ID: " + id);
        }

        // Asegurar que el ID del producto coincida con el ID del path
        producto.setId(id);

        // Validar datos del producto
        validarProducto(producto);

        return repository.update(producto);
    }

    @Override
    public boolean eliminarProducto(String id) {
        if (!repository.existsById(id)) {
            throw new ProductoNotFoundException("Producto no encontrado con ID: " + id);
        }
        return repository.deleteById(id);
    }

    @Override
    public boolean existeProducto(String id) {
        return repository.existsById(id);
    }

    @Override
    public int contarProductos() {
        return repository.count();
    }

    private void validarProducto(ProductoAgricola producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }

        if (!Validador.validarCadenaNoVacia(producto.getNombre())) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        if (producto.getHectareasCultivadas() != null &&
                !Validador.validarRangoHectareas(producto.getHectareasCultivadas())) {
            throw new IllegalArgumentException("Las hectáreas deben estar entre 0.1 y 10,000");
        }

        if (producto.getCantidadProducida() != null &&
                !Validador.validarNumeroPositivo(producto.getCantidadProducida())) {
            throw new IllegalArgumentException("La cantidad producida debe ser positiva");
        }

        if (producto.getPrecioVenta() != null &&
                !Validador.validarPrecioRazonable(producto.getPrecioVenta())) {
            throw new IllegalArgumentException("El precio de venta debe estar entre $100 y $1,000,000");
        }
    }
}