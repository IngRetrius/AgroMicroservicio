package co.unibague.agropecuario.rest.controller;

import co.unibague.agropecuario.rest.dto.ApiResponseDTO;
import co.unibague.agropecuario.rest.model.Cosecha;
import co.unibague.agropecuario.rest.service.CosechaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestión de Cosechas
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
@RestController
@RequestMapping("/api/cosechas")
public class CosechaController {

    @Autowired
    private CosechaService service;

    // ===== ENDPOINTS ESPECÍFICOS PRIMERO =====

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("CosechaController funcionando correctamente - Puerto 8081");
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseDTO<Object>> obtenerEstadisticasGenerales() {
        try {
            int totalCosechas = service.contarCosechas();
            Map<String, Object> estadisticas = new java.util.HashMap<>();
            estadisticas.put("totalCosechas", totalCosechas);
            estadisticas.put("servidor", "API REST Agropecuario - Cosechas");
            estadisticas.put("version", "2.0.0");
            estadisticas.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(ApiResponseDTO.success("Estadísticas generales de cosechas", estadisticas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    // ===== BÚSQUEDAS CON PARÁMETROS =====

    @GetMapping(params = "calidad")
    public ResponseEntity<ApiResponseDTO<List<Cosecha>>> buscarPorCalidad(
            @RequestParam("calidad") String calidad) {
        try {
            List<Cosecha> cosechas = service.obtenerCosechasPorCalidad(calidad);
            String mensaje = String.format("Se encontraron %d cosechas de calidad '%s'",
                    cosechas.size(), calidad);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, cosechas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al buscar por calidad: " + e.getMessage()));
        }
    }

    @GetMapping(params = "estado")
    public ResponseEntity<ApiResponseDTO<List<Cosecha>>> buscarPorEstado(
            @RequestParam("estado") String estado) {
        try {
            List<Cosecha> cosechas = service.obtenerCosechasPorEstado(estado);
            String mensaje = String.format("Se encontraron %d cosechas con estado '%s'",
                    cosechas.size(), estado);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, cosechas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al buscar por estado: " + e.getMessage()));
        }
    }

    @GetMapping(params = {"fechaInicio", "fechaFin"})
    public ResponseEntity<ApiResponseDTO<List<Cosecha>>> buscarPorRangoFechas(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<Cosecha> cosechas = service.obtenerCosechasPorRangoFechas(fechaInicio, fechaFin);
            String mensaje = String.format("Se encontraron %d cosechas en el rango de fechas",
                    cosechas.size());
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, cosechas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al buscar por rango de fechas: " + e.getMessage()));
        }
    }

    // ===== LISTAR TODAS (sin parámetros) =====

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Cosecha>>> listarTodas() {
        try {
            List<Cosecha> cosechas = service.obtenerTodasLasCosechas();
            return ResponseEntity.ok(ApiResponseDTO.success("Cosechas obtenidas exitosamente", cosechas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener cosechas: " + e.getMessage()));
        }
    }

    // ===== ENDPOINTS MAESTRO-DETALLE ⚠️ MUY IMPORTANTE =====

    /**
     * Obtiene todas las cosechas de un producto específico
     * Endpoint clave para la relación maestro-detalle
     * GET /api/productos/{productoId}/cosechas
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponseDTO<List<Cosecha>>> obtenerCosechasPorProducto(
            @PathVariable String productoId) {
        try {
            List<Cosecha> cosechas = service.obtenerCosechasPorProducto(productoId);
            String mensaje = String.format("Se encontraron %d cosechas para el producto '%s'",
                    cosechas.size(), productoId);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, cosechas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener cosechas del producto: " + e.getMessage()));
        }
    }

    /**
     * Obtiene estadísticas de cosechas para un producto específico
     * GET /api/productos/{productoId}/cosechas/estadisticas
     */
    @GetMapping("/producto/{productoId}/estadisticas")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> obtenerEstadisticasPorProducto(
            @PathVariable String productoId) {
        try {
            Map<String, Object> estadisticas = service.calcularEstadisticasPorProducto(productoId);
            return ResponseEntity.ok(ApiResponseDTO.success(
                    "Estadísticas de cosechas del producto " + productoId, estadisticas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al calcular estadísticas: " + e.getMessage()));
        }
    }

    /**
     * Cuenta las cosechas de un producto
     * GET /api/productos/{productoId}/cosechas/count
     */
    @GetMapping("/producto/{productoId}/count")
    public ResponseEntity<ApiResponseDTO<Integer>> contarCosechasPorProducto(
            @PathVariable String productoId) {
        try {
            int cantidad = service.contarCosechasPorProducto(productoId);
            return ResponseEntity.ok(ApiResponseDTO.success(
                    "Total de cosechas del producto " + productoId, cantidad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al contar cosechas: " + e.getMessage()));
        }
    }

    // ===== PATH VARIABLES (más genéricos) =====

    /**
     * Obtiene una cosecha específica por su ID
     * GET /api/cosechas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Cosecha>> obtenerPorId(@PathVariable String id) {
        try {
            Optional<Cosecha> cosecha = service.obtenerCosechaPorId(id);
            if (cosecha.isPresent()) {
                return ResponseEntity.ok(ApiResponseDTO.success("Cosecha encontrada", cosecha.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Cosecha no encontrada con ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener cosecha: " + e.getMessage()));
        }
    }

    // ===== MÉTODOS POST, PUT, DELETE =====

    /**
     * Crea una nueva cosecha
     * POST /api/cosechas
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<Cosecha>> crear(@Valid @RequestBody Cosecha cosecha) {
        try {
            Cosecha cosechaCreada = service.crearCosecha(cosecha);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Cosecha creada exitosamente", cosechaCreada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al crear cosecha: " + e.getMessage()));
        }
    }

    /**
     * Actualiza una cosecha existente
     * PUT /api/cosechas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Cosecha>> actualizar(
            @PathVariable String id, @Valid @RequestBody Cosecha cosecha) {
        try {
            Cosecha cosechaActualizada = service.actualizarCosecha(id, cosecha);
            return ResponseEntity.ok(ApiResponseDTO.success("Cosecha actualizada exitosamente", cosechaActualizada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al actualizar cosecha: " + e.getMessage()));
        }
    }

    /**
     * Elimina una cosecha
     * DELETE /api/cosechas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable String id) {
        try {
            service.eliminarCosecha(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Cosecha eliminada exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al eliminar cosecha: " + e.getMessage()));
        }
    }

    /**
     * Elimina todas las cosechas de un producto
     * DELETE /api/productos/{productoId}/cosechas
     */
    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponseDTO<Integer>> eliminarCosechasPorProducto(
            @PathVariable String productoId) {
        try {
            int eliminadas = service.eliminarCosechasPorProducto(productoId);
            String mensaje = String.format("Se eliminaron %d cosechas del producto %s",
                    eliminadas, productoId);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, eliminadas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al eliminar cosechas: " + e.getMessage()));
        }
    }
}