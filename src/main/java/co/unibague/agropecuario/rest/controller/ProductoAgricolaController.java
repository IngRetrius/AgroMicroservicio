package co.unibague.agropecuario.rest.controller;

import co.unibague.agropecuario.rest.dto.ApiResponseDTO;
import co.unibague.agropecuario.rest.model.ProductoAgricola;
import co.unibague.agropecuario.rest.service.ProductoAgricolaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoAgricolaController {

    @Autowired
    private ProductoAgricolaService service;

    /**
     * Listar todos los productos
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> listarTodos() {
        List<ProductoAgricola> productos = service.obtenerTodosLosProductos();
        return ResponseEntity.ok(ApiResponseDTO.success("Productos obtenidos exitosamente", productos));
    }

    /**
     * Buscar productos por tipo de cultivo
     */
    @GetMapping(params = "tipo")
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> listarPorTipo(
            @RequestParam("tipo") String tipo) {
        List<ProductoAgricola> productos = service.buscarPorTipoCultivo(tipo);
        String mensaje = String.format("Se encontraron %d productos del tipo '%s'", productos.size(), tipo);
        return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
    }

    /**
     * Buscar productos por nombre
     */
    @GetMapping(params = "nombre")
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> buscarPorNombre(
            @RequestParam("nombre") String nombre) {
        List<ProductoAgricola> productos = service.buscarPorNombre(nombre);
        String mensaje = String.format("Se encontraron %d productos con el nombre '%s'", productos.size(), nombre);
        return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
    }

    /**
     * Buscar productos por temporada
     */
    @GetMapping(params = "temporada")
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> buscarPorTemporada(
            @RequestParam("temporada") String temporada) {
        List<ProductoAgricola> productos = service.buscarPorTemporada(temporada);
        String mensaje = String.format("Se encontraron %d productos de la temporada '%s'", productos.size(), temporada);
        return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
    }

    /**
     * Buscar productos por rango de hectáreas
     */
    @GetMapping(params = {"hectareasMin", "hectareasMax"})
    public ResponseEntity<ApiResponseDTO<List<ProductoAgricola>>> buscarPorRangoHectareas(
            @RequestParam(value = "hectareasMin", required = false) Double hectareasMin,
            @RequestParam(value = "hectareasMax", required = false) Double hectareasMax) {
        List<ProductoAgricola> productos = service.buscarPorRangoHectareas(hectareasMin, hectareasMax);
        String mensaje = String.format("Se encontraron %d productos en el rango de hectáreas", productos.size());
        return ResponseEntity.ok(ApiResponseDTO.success(mensaje, productos));
    }

    /**
     * Obtener producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductoAgricola>> obtenerPorId(@PathVariable String id) {
        Optional<ProductoAgricola> producto = service.obtenerProductoPorId(id);

        if (producto.isPresent()) {
            return ResponseEntity.ok(
                    ApiResponseDTO.success("Producto encontrado", producto.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error("Producto no encontrado con ID: " + id));
        }
    }

    /**
     * Crear nuevo producto
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductoAgricola>> crear(@Valid @RequestBody ProductoAgricola producto) {
        ProductoAgricola productoCreado = service.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Producto creado exitosamente", productoCreado));
    }

    /**
     * Actualizar producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductoAgricola>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ProductoAgricola producto) {
        ProductoAgricola productoActualizado = service.actualizarProducto(id, producto);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Producto actualizado exitosamente", productoActualizado));
    }

    /**
     * Eliminar producto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable String id) {
        service.eliminarProducto(id);
        return ResponseEntity.ok(
                ApiResponseDTO.success("Producto eliminado exitosamente", null));
    }

    /**
     * Obtener estadísticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseDTO<Object>> obtenerEstadisticas() {
        int totalProductos = service.contarProductos();

        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
        estadisticas.put("totalProductos", totalProductos);
        estadisticas.put("servidor", "API REST Agropecuario");
        estadisticas.put("version", "2.0.0");
        estadisticas.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(
                ApiResponseDTO.success("Estadísticas del sistema", estadisticas));
    }
}