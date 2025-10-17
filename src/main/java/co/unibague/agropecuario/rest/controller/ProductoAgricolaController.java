package co.unibague.agropecuario.rest.controller;

import co.unibague.agropecuario.rest.dto.ApiResponseDTO;
import co.unibague.agropecuario.rest.model.Cosecha;
import co.unibague.agropecuario.rest.model.ProductoAgricola;
import co.unibague.agropecuario.rest.service.CosechaService;
import co.unibague.agropecuario.rest.service.ProductoAgricolaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para ProductoAgricola (MAESTRO)
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoAgricolaController {

    @Autowired
    private ProductoAgricolaService service;

    @Autowired
    private CosechaService cosechaService;

    // ===== ENDPOINTS ESPECÍFICOS PRIMERO =====

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Controller funcionando correctamente - Puerto 8081");
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseDTO<Object>> obtenerEstadisticas() {
        try {
            int totalProductos = service.contarProductos();
            int totalCosechas = cosechaService.contarCosechas();

            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            estadisticas.put("totalProductos", totalProductos);
            estadisticas.put("totalCosechas", totalCosechas);
            estadisticas.put("servidor", "API REST Agropecuario");
            estadisticas.put("version", "2.0.0");
            estadisticas.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(ApiResponseDTO.success("Estadísticas del sistema", estadisticas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    // ===== ENDPOINTS MAESTRO-DETALLE ⚠️ MUY IMPORTANTE =====

    /**
     * Obtiene todas las cosechas de un producto específico
     * GET /api/productos/{productoId}/cosechas
     */
    @GetMapping("/{productoId}/cosechas")
    public ResponseEntity<ApiResponseDTO<List<Cosecha>>> obtenerCosechasDelProducto(
            @PathVariable String productoId) {
        try {
            // Verificar que el producto exista
            if (!service.existeProducto(productoId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Producto no encontrado con ID: " + productoId));
            }

            List<Cosecha> cosechas = cosechaService.obtenerCosechasPorProducto(productoId);
            String mensaje = String.format("Se encontraron %d cosechas para el producto '%s'",
                    cosechas.size(), productoId);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, cosechas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener cosechas: " + e.getMessage()));
        }
    }

    /**
     * Crea una nueva cosecha para un producto específico
     * POST /api/productos/{productoId}/cosechas
     */
    @PostMapping("/{productoId}/cosechas")
    public ResponseEntity<ApiResponseDTO<Cosecha>> crearCosechaParaProducto(
            @PathVariable String productoId,
            @Valid @RequestBody Cosecha cosecha) {
        try {
            // Verificar que el producto exista
            if (!service.existeProducto(productoId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Producto no encontrado con ID: " + productoId));
            }

            // Asegurar que el productoId del path coincida con el del body
            cosecha.setProductoId(productoId);

            Cosecha cosechaCreada = cosechaService.crearCosecha(cosecha);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Cosecha creada exitosamente para el producto " + productoId, cosechaCreada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al crear cosecha: " + e.getMessage()));
        }
    }

    /**
     * Obtiene una cosecha específica de un producto
     * GET /api/productos/{productoId}/cosechas/{cosechaId}
     */
    @GetMapping("/{productoId}/cosechas/{cosechaId}")
    public ResponseEntity<ApiResponseDTO<Cosecha>> obtenerCosechaDelProducto(
            @PathVariable String productoId,
            @PathVariable String cosechaId) {
        try {
            // Verificar que el producto exista
            if (!service.existeProducto(productoId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Producto no encontrado con ID: " + productoId));
            }

            Optional<Cosecha> cosecha = cosechaService.obtenerCosechaPorId(cosechaId);
            if (cosecha.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Cosecha no encontrada con ID: " + cosechaId));
            }

            // Verificar que la cosecha pertenece al producto
            if (!cosecha.get().getProductoId().equals(productoId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("La cosecha " + cosechaId + " no pertenece al producto " + productoId));
            }

            return ResponseEntity.ok(ApiResponseDTO.success("Cosecha encontrada", cosecha.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener cosecha: " + e.getMessage()));
        }
    }

    /**
     * Actualiza una cosecha específica de un producto
     * PUT /api/productos/{productoId}/cosechas/{cosechaId}
     */
    @PutMapping("/{productoId}/cosechas/{cosechaId}")
    public ResponseEntity<ApiResponseDTO<Cosecha>> actualizarCosechaDelProducto(
            @PathVariable String productoId,
            @PathVariable String cosechaId,
            @Valid @RequestBody Cosecha cosecha) {
        try {
            // Verificar que el producto exista
            if (!service.existeProducto(productoId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Producto no encontrado con ID: " + productoId));
            }

            // Verificar que la cosecha exista
            Optional<Cosecha> cosechaExistente = cosechaService.obtenerCosechaPorId(cosechaId);
            if (cosechaExistente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Cosecha no encontrada con ID: " + cosechaId));
            }

            // Verificar que la cosecha pertenece al producto
            if (!cosechaExistente.get().getProductoId().equals(productoId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("La cosecha " + cosechaId + " no pertenece al producto " + productoId));
            }

            // Asegurar que los IDs sean correctos
            cosecha.setId(cosechaId);
            cosecha.setProductoId(productoId);

            Cosecha cosechaActualizada = cosechaService.actualizarCosecha(cosechaId, cosecha);
            return ResponseEntity.ok(ApiResponseDTO.success("Cosecha actualizada exitosamente", cosechaActualizada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al actualizar cosecha: " + e.getMessage()));
        }
    }

    /**
     * Elimina una cosecha específica de un producto
     * DELETE /api/productos/{productoId}/cosechas/{cosechaId}
     */
    @DeleteMapping("/{productoId}/cosechas/{cosechaId}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarCosechaDelProducto(
            @PathVariable String productoId,
            @PathVariable String cosechaId) {
        try {
            // Verificar que el producto exista
            if (!service.existeProducto(productoId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Producto no encontrado con ID: " + productoId));
            }

            // Verificar que la cosecha exista y pertenezca al producto
            Optional<Cosecha> cosecha = cosechaService.obtenerCosechaPorId(cosechaId);
            if (cosecha.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Cosecha no encontrada con ID: " + cosechaId));
            }

            if (!cosecha.get().getProductoId().equals(productoId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("La cosecha " + cosechaId + " no pertenece al producto " + productoId));
            }

            cosechaService.eliminarCosecha(cosechaId);
            return ResponseEntity.ok(ApiResponseDTO.success("Cosecha eliminada exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al eliminar cosecha: " + e.getMessage()));
        }
    }

    /**
     * Obtiene estadísticas de cosechas para un producto
     * GET /api/productos/{productoId}/cosechas/estadisticas
     */
    @GetMapping("/{productoId}/cosechas/estadisticas")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> obtenerEstadisticasCosechas(
            @PathVariable String productoId) {
        try {
            if (!service.existeProducto(productoId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Producto no encontrado con ID: " + productoId));
            }

            Map<String, Object> estadisticas = cosechaService.calcularEstadisticasPorProducto(productoId);
            return ResponseEntity.ok(ApiResponseDTO.success(
                    "Estadísticas de cosechas del producto " + productoId, estadisticas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al calcular estadísticas: " + e.getMessage()));
        }
    }

    // ===== BÚSQUEDAS CON PARÁMETROS =====

    @GetMapping(params = "tipo")
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> buscarPorTipo(
            @RequestParam("tipo") String tipo) {
        try {
            List<ProductoAgricola> productos = service.buscarPorTipoCultivo(tipo);
            String mensaje = String.format("Se encontraron %d productos del tipo '%s'", productos.size(), tipo);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al buscar por tipo: " + e.getMessage()));
        }
    }

    @GetMapping(params = "nombre")
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> buscarPorNombre(
            @RequestParam("nombre") String nombre) {
        try {
            List<ProductoAgricola> productos = service.buscarPorNombre(nombre);
            String mensaje = String.format("Se encontraron %d productos con el nombre '%s'", productos.size(), nombre);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al buscar por nombre: " + e.getMessage()));
        }
    }

    @GetMapping(params = "temporada")
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> buscarPorTemporada(
            @RequestParam("temporada") String temporada) {
        try {
            List<ProductoAgricola> productos = service.buscarPorTemporada(temporada);
            String mensaje = String.format("Se encontraron %d productos de la temporada '%s'", productos.size(), temporada);
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al buscar por temporada: " + e.getMessage()));
        }
    }

    @GetMapping(params = {"hectareasMin", "hectareasMax"})
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> buscarPorRangoHectareas(
            @RequestParam(value = "hectareasMin", required = false) Double hectareasMin,
            @RequestParam(value = "hectareasMax", required = false) Double hectareasMax) {
        try {
            List<ProductoAgricola> productos = service.buscarPorRangoHectareas(hectareasMin, hectareasMax);
            String mensaje = String.format("Se encontraron %d productos en el rango de hectáreas", productos.size());
            return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al buscar por rango: " + e.getMessage()));
        }
    }

    // ===== LISTAR TODOS =====

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> listarTodos() {
        try {
            List<ProductoAgricola> productos = service.obtenerTodosLosProductos();
            return ResponseEntity.ok(ApiResponseDTO.success("Productos obtenidos exitosamente", productos));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener productos: " + e.getMessage()));
        }
    }

    // ===== PATH VARIABLES =====

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductoAgricola>> obtenerPorId(@PathVariable String id) {
        try {
            Optional<ProductoAgricola> producto = service.obtenerProductoPorId(id);
            if (producto.isPresent()) {
                return ResponseEntity.ok(ApiResponseDTO.success("Producto encontrado", producto.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Producto no encontrado con ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener producto: " + e.getMessage()));
        }
    }

    // ===== MÉTODOS POST, PUT, DELETE =====

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductoAgricola>> crear(@Valid @RequestBody ProductoAgricola producto) {
        try {
            ProductoAgricola productoCreado = service.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success("Producto creado exitosamente", productoCreado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al crear producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductoAgricola>> actualizar(
            @PathVariable String id, @Valid @RequestBody ProductoAgricola producto) {
        try {
            ProductoAgricola productoActualizado = service.actualizarProducto(id, producto);
            return ResponseEntity.ok(ApiResponseDTO.success("Producto actualizado exitosamente", productoActualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al actualizar producto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable String id) {
        try {
            service.eliminarProducto(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Producto eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al eliminar producto: " + e.getMessage()));
        }
    }
}