package co.unibague.agropecuario.rest.repository.impl;

import co.unibague.agropecuario.rest.model.Cosecha;
import co.unibague.agropecuario.rest.repository.CosechaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Cosechas
 * Almacenamiento en memoria con ConcurrentHashMap
 */
@Repository
public class CosechaRepositoryImpl implements CosechaRepository {

    private final ConcurrentHashMap<String, Cosecha> cosechas = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    public CosechaRepositoryImpl() {
        inicializarDatosPrueba();
    }

    /**
     * Inicializa 8-10 cosechas de prueba para los productos existentes
     */
    private void inicializarDatosPrueba() {
        // ===== 3 COSECHAS PARA AGR001 (Café Arábica Premium) =====

        Cosecha cosecha1 = new Cosecha();
        cosecha1.setId("COS001");
        cosecha1.setProductoId("AGR001");
        cosecha1.setFechaCosecha(LocalDateTime.now().minusDays(10));
        cosecha1.setCantidadRecolectada(2500);
        cosecha1.setCalidadProducto("Premium");
        cosecha1.setNumeroTrabajadores(8);
        cosecha1.setCostoManoObra(960000.0);
        cosecha1.setCondicionesClimaticas("Soleado");
        cosecha1.setEstadoCosecha("Completada");
        cosecha1.setObservaciones("Excelente cosecha, granos de alta calidad");
        cosechas.put(cosecha1.getId(), cosecha1);

        Cosecha cosecha2 = new Cosecha();
        cosecha2.setId("COS002");
        cosecha2.setProductoId("AGR001");
        cosecha2.setFechaCosecha(LocalDateTime.now().minusDays(40));
        cosecha2.setCantidadRecolectada(3500);
        cosecha2.setCalidadProducto("Extra");
        cosecha2.setNumeroTrabajadores(10);
        cosecha2.setCostoManoObra(1200000.0);
        cosecha2.setCondicionesClimaticas("Parcialmente nublado");
        cosecha2.setEstadoCosecha("Completada");
        cosecha2.setObservaciones("Buen rendimiento general");
        cosechas.put(cosecha2.getId(), cosecha2);

        Cosecha cosecha3 = new Cosecha();
        cosecha3.setId("COS003");
        cosecha3.setProductoId("AGR001");
        cosecha3.setFechaCosecha(LocalDateTime.now().minusDays(75));
        cosecha3.setCantidadRecolectada(3500);
        cosecha3.setCalidadProducto("Estándar");
        cosecha3.setNumeroTrabajadores(12);
        cosecha3.setCostoManoObra(1440000.0);
        cosecha3.setCondicionesClimaticas("Lluvioso");
        cosecha3.setEstadoCosecha("Completada");
        cosecha3.setObservaciones("Afectado por lluvias, calidad regular");
        cosechas.put(cosecha3.getId(), cosecha3);

        // ===== 2 COSECHAS PARA AGR002 (Arroz Fedearroz 67) =====

        Cosecha cosecha4 = new Cosecha();
        cosecha4.setId("COS004");
        cosecha4.setProductoId("AGR002");
        cosecha4.setFechaCosecha(LocalDateTime.now().minusDays(20));
        cosecha4.setCantidadRecolectada(42000);
        cosecha4.setCalidadProducto("Extra");
        cosecha4.setNumeroTrabajadores(25);
        cosecha4.setCostoManoObra(3750000.0);
        cosecha4.setCondicionesClimaticas("Soleado");
        cosecha4.setEstadoCosecha("Completada");
        cosecha4.setObservaciones("Cosecha principal, excelentes resultados");
        cosechas.put(cosecha4.getId(), cosecha4);

        Cosecha cosecha5 = new Cosecha();
        cosecha5.setId("COS005");
        cosecha5.setProductoId("AGR002");
        cosecha5.setFechaCosecha(LocalDateTime.now().minusDays(55));
        cosecha5.setCantidadRecolectada(36000);
        cosecha5.setCalidadProducto("Estándar");
        cosecha5.setNumeroTrabajadores(22);
        cosecha5.setCostoManoObra(3300000.0);
        cosecha5.setCondicionesClimaticas("Nublado");
        cosecha5.setEstadoCosecha("Completada");
        cosecha5.setObservaciones("Segunda cosecha del semestre");
        cosechas.put(cosecha5.getId(), cosecha5);

        // ===== 3 COSECHAS PARA AGR003 (Cacao Trinitario) =====

        Cosecha cosecha6 = new Cosecha();
        cosecha6.setId("COS006");
        cosecha6.setProductoId("AGR003");
        cosecha6.setFechaCosecha(LocalDateTime.now().minusDays(5));
        cosecha6.setCantidadRecolectada(850);
        cosecha6.setCalidadProducto("Premium");
        cosecha6.setNumeroTrabajadores(6);
        cosecha6.setCostoManoObra(720000.0);
        cosecha6.setCondicionesClimaticas("Soleado");
        cosecha6.setEstadoCosecha("Completada");
        cosecha6.setObservaciones("Mazorcas de excelente tamaño y calidad");
        cosechas.put(cosecha6.getId(), cosecha6);

        Cosecha cosecha7 = new Cosecha();
        cosecha7.setId("COS007");
        cosecha7.setProductoId("AGR003");
        cosecha7.setFechaCosecha(LocalDateTime.now().minusDays(35));
        cosecha7.setCantidadRecolectada(750);
        cosecha7.setCalidadProducto("Extra");
        cosecha7.setNumeroTrabajadores(5);
        cosecha7.setCostoManoObra(600000.0);
        cosecha7.setCondicionesClimaticas("Parcialmente nublado");
        cosecha7.setEstadoCosecha("Completada");
        cosecha7.setObservaciones("Buena fermentación del cacao");
        cosechas.put(cosecha7.getId(), cosecha7);

        Cosecha cosecha8 = new Cosecha();
        cosecha8.setId("COS008");
        cosecha8.setProductoId("AGR003");
        cosecha8.setFechaCosecha(LocalDateTime.now().minusDays(70));
        cosecha8.setCantidadRecolectada(800);
        cosecha8.setCalidadProducto("Estándar");
        cosecha8.setNumeroTrabajadores(6);
        cosecha8.setCostoManoObra(720000.0);
        cosecha8.setCondicionesClimaticas("Lluvioso");
        cosecha8.setEstadoCosecha("Completada");
        cosecha8.setObservaciones("Proceso de secado más largo debido a lluvias");
        cosechas.put(cosecha8.getId(), cosecha8);

        idCounter.set(8);
    }

    @Override
    public Cosecha save(Cosecha cosecha) {
        if (cosecha.getId() == null || cosecha.getId().isEmpty()) {
            cosecha.setId(generateNextId());
        }
        cosechas.put(cosecha.getId(), cosecha);
        return cosecha;
    }

    @Override
    public Optional<Cosecha> findById(String id) {
        return Optional.ofNullable(cosechas.get(id));
    }

    @Override
    public List<Cosecha> findAll() {
        return new ArrayList<>(cosechas.values());
    }

    @Override
    public List<Cosecha> findByProductoId(String productoId) {
        return cosechas.values().stream()
                .filter(c -> c.getProductoId().equals(productoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Cosecha> findByCalidad(String calidad) {
        return cosechas.values().stream()
                .filter(c -> c.getCalidadProducto() != null &&
                        c.getCalidadProducto().equalsIgnoreCase(calidad))
                .collect(Collectors.toList());
    }

    @Override
    public List<Cosecha> findByEstado(String estado) {
        return cosechas.values().stream()
                .filter(c -> c.getEstadoCosecha() != null &&
                        c.getEstadoCosecha().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    @Override
    public List<Cosecha> findByFechaRange(LocalDateTime inicio, LocalDateTime fin) {
        return cosechas.values().stream()
                .filter(c -> {
                    LocalDateTime fecha = c.getFechaCosecha();
                    if (fecha == null) return false;
                    boolean despuesInicio = inicio == null || !fecha.isBefore(inicio);
                    boolean antesFin = fin == null || !fecha.isAfter(fin);
                    return despuesInicio && antesFin;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Cosecha update(Cosecha cosecha) {
        if (cosechas.containsKey(cosecha.getId())) {
            cosechas.put(cosecha.getId(), cosecha);
            return cosecha;
        }
        return null;
    }

    @Override
    public boolean deleteById(String id) {
        return cosechas.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return cosechas.containsKey(id);
    }

    @Override
    public int count() {
        return cosechas.size();
    }

    @Override
    public int countByProductoId(String productoId) {
        return (int) cosechas.values().stream()
                .filter(c -> c.getProductoId().equals(productoId))
                .count();
    }

    @Override
    public String generateNextId() {
        int nextId = idCounter.incrementAndGet();
        return String.format("COS%03d", nextId);
    }

    @Override
    public int deleteByProductoId(String productoId) {
        List<String> idsAEliminar = cosechas.values().stream()
                .filter(c -> c.getProductoId().equals(productoId))
                .map(Cosecha::getId)
                .collect(Collectors.toList());

        idsAEliminar.forEach(cosechas::remove);
        return idsAEliminar.size();
    }
}