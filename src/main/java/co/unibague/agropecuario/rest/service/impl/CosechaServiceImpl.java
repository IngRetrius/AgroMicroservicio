package co.unibague.agropecuario.rest.service.impl;

import co.unibague.agropecuario.rest.exception.CosechaNotFoundException;
import co.unibague.agropecuario.rest.exception.ProductoNotFoundException;
import co.unibague.agropecuario.rest.exception.ValidationException;
import co.unibague.agropecuario.rest.model.Cosecha;
import co.unibague.agropecuario.rest.model.ProductoAgricola;
import co.unibague.agropecuario.rest.repository.CosechaJpaRepository;
import co.unibague.agropecuario.rest.repository.ProductoAgricolaJpaRepository;
import co.unibague.agropecuario.rest.service.CosechaService;
import co.unibague.agropecuario.rest.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Cosechas usando JPA/Hibernate
 * Universidad de Ibagué - Tercer Prototipo
 */
@Service
@Transactional
public class CosechaServiceImpl implements CosechaService {

    @Autowired
    private CosechaJpaRepository cosechaRepository;

    @Autowired
    private ProductoAgricolaJpaRepository productoRepository;

    @Autowired
    private IdGeneratorService idGenerator;

    @Override
    public Cosecha crearCosecha(Cosecha cosecha) {
        // Validar datos de la cosecha
        validarCosecha(cosecha);

        // Obtener el producto y establecer la relación
        String productoId = cosecha.getProductoId();
        ProductoAgricola producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "No se puede crear la cosecha. El producto con ID '" +
                                productoId + "' no existe"));

        // Establecer la relación @ManyToOne
        cosecha.setProducto(producto);

        // Generar ID si no lo tiene
        if (cosecha.getId() == null || cosecha.getId().trim().isEmpty()) {
            cosecha.setId(idGenerator.generateNextCosechaId());
        }

        // Establecer valores por defecto
        if (cosecha.getFechaCosecha() == null) {
            cosecha.setFechaCosecha(LocalDateTime.now());
        }
        if (cosecha.getEstadoCosecha() == null || cosecha.getEstadoCosecha().isEmpty()) {
            cosecha.setEstadoCosecha("Pendiente");
        }

        return cosechaRepository.save(cosecha);
    }

    @Override
    public Optional<Cosecha> obtenerCosechaPorId(String id) {
        return cosechaRepository.findById(id);
    }

    @Override
    public List<Cosecha> obtenerTodasLasCosechas() {
        return cosechaRepository.findAll();
    }

    @Override
    public List<Cosecha> obtenerCosechasPorProducto(String productoId) {
        if (productoId == null || productoId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del producto no puede estar vacío");
        }

        // Validar que el producto exista
        if (!productoRepository.existsById(productoId)) {
            throw new ProductoNotFoundException(
                    "El producto con ID '" + productoId + "' no existe");
        }

        return cosechaRepository.findCosechasByProductoId(productoId);
    }

    @Override
    public List<Cosecha> obtenerCosechasPorCalidad(String calidad) {
        if (calidad == null || calidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La calidad no puede estar vacía");
        }
        return cosechaRepository.findByCalidadProducto(calidad);
    }

    @Override
    public List<Cosecha> obtenerCosechasPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        return cosechaRepository.findByEstadoCosecha(estado);
    }

    @Override
    public List<Cosecha> obtenerCosechasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (inicio == null) inicio = LocalDateTime.of(2000, 1, 1, 0, 0);
        if (fin == null) fin = LocalDateTime.now();

        return cosechaRepository.findByFechaCosechaBetween(inicio, fin);
    }

    @Override
    public Cosecha actualizarCosecha(String id, Cosecha cosecha) {
        // Verificar que existe
        if (!cosechaRepository.existsById(id)) {
            throw new CosechaNotFoundException("Cosecha no encontrada con ID: " + id);
        }

        // Asegurar que el ID coincida
        cosecha.setId(id);

        // Validar datos
        validarCosecha(cosecha);

        // Obtener el producto y establecer la relación
        String productoId = cosecha.getProductoId();
        ProductoAgricola producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "No se puede actualizar la cosecha. El producto con ID '" +
                                productoId + "' no existe"));

        // Establecer la relación @ManyToOne
        cosecha.setProducto(producto);

        return cosechaRepository.save(cosecha);
    }

    @Override
    public boolean eliminarCosecha(String id) {
        if (!cosechaRepository.existsById(id)) {
            throw new CosechaNotFoundException("Cosecha no encontrada con ID: " + id);
        }
        cosechaRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existeCosecha(String id) {
        return cosechaRepository.existsById(id);
    }

    @Override
    public int contarCosechas() {
        return (int) cosechaRepository.count();
    }

    @Override
    public int contarCosechasPorProducto(String productoId) {
        if (productoId == null || productoId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del producto no puede estar vacío");
        }

        // Obtener el producto y contar sus cosechas
        ProductoAgricola producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "El producto con ID '" + productoId + "' no existe"));

        Long count = cosechaRepository.countByProducto(producto);
        return count != null ? count.intValue() : 0;
    }

    @Override
    public int eliminarCosechasPorProducto(String productoId) {
        if (productoId == null || productoId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del producto no puede estar vacío");
        }

        // Obtener las cosechas del producto y eliminarlas
        List<Cosecha> cosechas = cosechaRepository.findCosechasByProductoId(productoId);
        cosechaRepository.deleteAll(cosechas);
        return cosechas.size();
    }

    @Override
    public Map<String, Object> calcularEstadisticasPorProducto(String productoId) {
        List<Cosecha> cosechas = obtenerCosechasPorProducto(productoId);

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("productoId", productoId);
        estadisticas.put("totalCosechas", cosechas.size());

        if (cosechas.isEmpty()) {
            estadisticas.put("cantidadTotalRecolectada", 0);
            estadisticas.put("promedioRecoleccion", 0.0);
            estadisticas.put("costoTotalManoObra", 0.0);
            estadisticas.put("totalTrabajadores", 0);
            return estadisticas;
        }

        // Calcular totales
        int cantidadTotal = cosechas.stream()
                .mapToInt(c -> c.getCantidadRecolectada() != null ? c.getCantidadRecolectada() : 0)
                .sum();

        double costoTotal = cosechas.stream()
                .mapToDouble(c -> c.getCostoManoObra() != null ? c.getCostoManoObra() : 0.0)
                .sum();

        int totalTrabajadores = cosechas.stream()
                .mapToInt(c -> c.getNumeroTrabajadores() != null ? c.getNumeroTrabajadores() : 0)
                .sum();

        double promedioRecoleccion = (double) cantidadTotal / cosechas.size();

        // Calcular distribución por calidad
        Map<String, Long> distribucionCalidad = cosechas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCalidadProducto() != null ? c.getCalidadProducto() : "Sin especificar",
                        Collectors.counting()));

        // Calcular distribución por estado
        Map<String, Long> distribucionEstado = cosechas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getEstadoCosecha() != null ? c.getEstadoCosecha() : "Sin especificar",
                        Collectors.counting()));

        // Encontrar mejor y peor cosecha
        Optional<Cosecha> mejorCosecha = cosechas.stream()
                .max(Comparator.comparing(c -> c.getCantidadRecolectada() != null ?
                        c.getCantidadRecolectada() : 0));
        Optional<Cosecha> peorCosecha = cosechas.stream()
                .min(Comparator.comparing(c -> c.getCantidadRecolectada() != null ?
                        c.getCantidadRecolectada() : 0));

        // Construir respuesta
        estadisticas.put("cantidadTotalRecolectada", cantidadTotal);
        estadisticas.put("promedioRecoleccion", Math.round(promedioRecoleccion * 100.0) / 100.0);
        estadisticas.put("costoTotalManoObra", Math.round(costoTotal * 100.0) / 100.0);
        estadisticas.put("totalTrabajadores", totalTrabajadores);
        estadisticas.put("distribucionPorCalidad", distribucionCalidad);
        estadisticas.put("distribucionPorEstado", distribucionEstado);

        if (mejorCosecha.isPresent()) {
            estadisticas.put("mejorCosechaId", mejorCosecha.get().getId());
            estadisticas.put("mejorCosechaCantidad", mejorCosecha.get().getCantidadRecolectada());
        }
        if (peorCosecha.isPresent()) {
            estadisticas.put("peorCosechaId", peorCosecha.get().getId());
            estadisticas.put("peorCosechaCantidad", peorCosecha.get().getCantidadRecolectada());
        }

        return estadisticas;
    }

    // ===== MÉTODOS PRIVADOS DE VALIDACIÓN =====

    private void validarCosecha(Cosecha cosecha) {
        if (cosecha == null) {
            throw new ValidationException("La cosecha no puede ser null");
        }

        if (cosecha.getProductoId() == null || cosecha.getProductoId().trim().isEmpty()) {
            throw new ValidationException("El ID del producto es obligatorio");
        }

        if (cosecha.getCantidadRecolectada() == null || cosecha.getCantidadRecolectada() <= 0) {
            throw new ValidationException("La cantidad recolectada debe ser mayor a 0");
        }

        if (cosecha.getCalidadProducto() == null || cosecha.getCalidadProducto().trim().isEmpty()) {
            throw new ValidationException("La calidad del producto es obligatoria");
        }

        if (cosecha.getFechaCosecha() != null && cosecha.getFechaCosecha().isAfter(LocalDateTime.now())) {
            throw new ValidationException("La fecha de cosecha no puede ser futura");
        }

        if (cosecha.getNumeroTrabajadores() != null && cosecha.getNumeroTrabajadores() < 0) {
            throw new ValidationException("El número de trabajadores no puede ser negativo");
        }

        if (cosecha.getCostoManoObra() != null && cosecha.getCostoManoObra() < 0) {
            throw new ValidationException("El costo de mano de obra no puede ser negativo");
        }
    }
}