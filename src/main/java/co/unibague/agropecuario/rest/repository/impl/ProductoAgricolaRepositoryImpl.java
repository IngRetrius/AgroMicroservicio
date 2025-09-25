package co.unibague.agropecuario.rest.repository.impl;

import co.unibague.agropecuario.rest.model.ProductoAgricola;
import co.unibague.agropecuario.rest.repository.ProductoAgricolaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ProductoAgricolaRepositoryImpl implements ProductoAgricolaRepository {

    private final ConcurrentHashMap<String, ProductoAgricola> productos = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    public ProductoAgricolaRepositoryImpl() {
        inicializarDatosPrueba();
    }

    private void inicializarDatosPrueba() {
        // Producto 1: Café
        ProductoAgricola cafe = new ProductoAgricola();
        cafe.setId("AGR001");
        cafe.setNombre("Café Arábica Premium");
        cafe.setTipoCultivo("Café");
        cafe.setHectareasCultivadas(5.5);
        cafe.setCantidadProducida(9500);
        cafe.setFechaProduccion(LocalDateTime.now().minusDays(15));
        cafe.setPrecioVenta(8500.0);
        cafe.setCostoProduccion(2800000.0);
        cafe.setRendimientoPorHa(1.8);
        cafe.setTemporada("Cosecha principal");
        cafe.setTipoSuelo("Franco arcilloso");
        cafe.setCodigoFinca("F001");
        productos.put(cafe.getId(), cafe);

        // Producto 2: Arroz
        ProductoAgricola arroz = new ProductoAgricola();
        arroz.setId("AGR002");
        arroz.setNombre("Arroz Fedearroz 67");
        arroz.setTipoCultivo("Arroz");
        arroz.setHectareasCultivadas(12.0);
        arroz.setCantidadProducida(78000);
        arroz.setFechaProduccion(LocalDateTime.now().minusDays(30));
        arroz.setPrecioVenta(1850.0);
        arroz.setCostoProduccion(4800000.0);
        arroz.setRendimientoPorHa(6.5);
        arroz.setTemporada("Temporada seca");
        arroz.setTipoSuelo("Franco");
        arroz.setCodigoFinca("F002");
        productos.put(arroz.getId(), arroz);

        // Producto 3: Cacao
        ProductoAgricola cacao = new ProductoAgricola();
        cacao.setId("AGR003");
        cacao.setNombre("Cacao Trinitario");
        cacao.setTipoCultivo("Cacao");
        cacao.setHectareasCultivadas(3.2);
        cacao.setCantidadProducida(2400);
        cacao.setFechaProduccion(LocalDateTime.now().minusDays(45));
        cacao.setPrecioVenta(12000.0);
        cacao.setCostoProduccion(1800000.0);
        cacao.setRendimientoPorHa(0.75);
        cacao.setTemporada("Todo el año");
        cacao.setTipoSuelo("Franco arcilloso");
        cacao.setCodigoFinca("F003");
        productos.put(cacao.getId(), cacao);

        idCounter.set(3);
    }

    @Override
    public ProductoAgricola save(ProductoAgricola producto) {
        if (producto.getId() == null || producto.getId().isEmpty()) {
            producto.setId(generateNextId());
        }
        productos.put(producto.getId(), producto);
        return producto;
    }

    @Override
    public Optional<ProductoAgricola> findById(String id) {
        return Optional.ofNullable(productos.get(id));
    }

    @Override
    public List<ProductoAgricola> findAll() {
        return new ArrayList<>(productos.values());
    }

    @Override
    public List<ProductoAgricola> findByTipoCultivo(String tipo) {
        return productos.values().stream()
                .filter(p -> p.getTipoCultivo().toLowerCase().contains(tipo.toLowerCase()))
                .toList();
    }

    @Override
    public List<ProductoAgricola> findByNombreContaining(String nombre) {
        return productos.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public List<ProductoAgricola> findByTemporada(String temporada) {
        return productos.values().stream()
                .filter(p -> p.getTemporada() != null &&
                        p.getTemporada().toLowerCase().contains(temporada.toLowerCase()))
                .toList();
    }

    @Override
    public List<ProductoAgricola> findByHectareasRange(Double minHectareas, Double maxHectareas) {
        return productos.values().stream()
                .filter(p -> {
                    Double hectareas = p.getHectareasCultivadas();
                    if (hectareas == null) return false;
                    boolean cumpleMin = minHectareas == null || hectareas >= minHectareas;
                    boolean cumpleMax = maxHectareas == null || hectareas <= maxHectareas;
                    return cumpleMin && cumpleMax;
                })
                .toList();
    }

    @Override
    public ProductoAgricola update(ProductoAgricola producto) {
        if (productos.containsKey(producto.getId())) {
            productos.put(producto.getId(), producto);
            return producto;
        }
        return null;
    }

    @Override
    public boolean deleteById(String id) {
        return productos.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return productos.containsKey(id);
    }

    @Override
    public int count() {
        return productos.size();
    }

    @Override
    public String generateNextId() {
        int nextId = idCounter.incrementAndGet();
        return String.format("AGR%03d", nextId);
    }
}