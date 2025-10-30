package co.unibague.agropecuario.rest.service;

import co.unibague.agropecuario.rest.repository.ProductoAgricolaJpaRepository;
import co.unibague.agropecuario.rest.repository.CosechaJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servicio para generación de IDs únicos
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
@Service
public class IdGeneratorService {

    @Autowired
    private ProductoAgricolaJpaRepository productoRepository;

    @Autowired
    private CosechaJpaRepository cosechaRepository;

    private final AtomicInteger productoCounter = new AtomicInteger(0);
    private final AtomicInteger cosechaCounter = new AtomicInteger(0);

    /**
     * Genera el siguiente ID para ProductoAgricola: AGR001, AGR002, etc.
     */
    public synchronized String generateNextProductoId() {
        if (productoCounter.get() == 0) {
            inicializarContadorProductos();
        }
        int nextId = productoCounter.incrementAndGet();
        return String.format("AGR%03d", nextId);
    }

    /**
     * Genera el siguiente ID para Cosecha: COS001, COS002, etc.
     */
    public synchronized String generateNextCosechaId() {
        if (cosechaCounter.get() == 0) {
            inicializarContadorCosechas();
        }
        int nextId = cosechaCounter.incrementAndGet();
        return String.format("COS%03d", nextId);
    }

    /**
     * Inicializa el contador de productos basándose en los IDs existentes
     */
    private void inicializarContadorProductos() {
        long count = productoRepository.count();

        // Si hay registros, obtener el último ID para calcular el próximo número
        if (count > 0) {
            // Buscar el ID más alto
            String lastId = productoRepository.findAll()
                    .stream()
                    .map(p -> p.getId())
                    .filter(id -> id != null && id.matches("AGR\\d{3}"))
                    .max(String::compareTo)
                    .orElse("AGR000");

            // Extraer el número del ID (ej: "AGR003" -> 3)
            String numberPart = lastId.substring(3);
            productoCounter.set(Integer.parseInt(numberPart));
        }
    }

    /**
     * Inicializa el contador de cosechas basándose en los IDs existentes
     */
    private void inicializarContadorCosechas() {
        long count = cosechaRepository.count();

        // Si hay registros, obtener el último ID para calcular el próximo número
        if (count > 0) {
            // Buscar el ID más alto
            String lastId = cosechaRepository.findAll()
                    .stream()
                    .map(c -> c.getId())
                    .filter(id -> id != null && id.matches("COS\\d{3}"))
                    .max(String::compareTo)
                    .orElse("COS000");

            // Extraer el número del ID (ej: "COS003" -> 3)
            String numberPart = lastId.substring(3);
            cosechaCounter.set(Integer.parseInt(numberPart));
        }
    }

    /**
     * Resetea los contadores (útil para testing)
     */
    public void resetContadores() {
        productoCounter.set(0);
        cosechaCounter.set(0);
    }
}
