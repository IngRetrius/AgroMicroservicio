package co.unibague.agropecuario.rest.controller;

import co.unibague.agropecuario.rest.dto.ApiResponseDTO;
import co.unibague.agropecuario.rest.model.ProductoAgricola;
import co.unibague.agropecuario.rest.service.ProductoAgricolaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoAgricolaController {

    @Autowired
    private ProductoAgricolaService service;

    // ENDPOINTS ESPECÍFICOS PRIMERO (antes que los genéricos)

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Controller funcionando correctamente - Puerto 8081");
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseDTO<Object>> obtenerEstadisticas() {
        try {
            int totalProductos = service.contarProductos();
            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            estadisticas.put("totalProductos", totalProductos);
            estadisticas.put("servidor", "API REST Agropecuario");
            estadisticas.put("version", "2.0.0");
            estadisticas.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(ApiResponseDTO.success("Estadísticas del sistema", estadisticas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    // BÚSQUEDAS CON PARÁMETROS

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

    // LISTAR TODOS (sin parámetros) - DESPUÉS de los específicos

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

    // PATH VARIABLES AL FINAL (más genéricos)

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

    // MÉTODOS POST, PUT, DELETE

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