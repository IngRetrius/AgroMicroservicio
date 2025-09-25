package co.unibague.agropecuario.rest.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO para peticiones de búsqueda de productos agrícolas
 */
public class BusquedaRequestDTO {

    private String nombre;
    private String tipoCultivo;
    private String temporada;
    private String tipoSuelo;
    private String codigoFinca;

    @DecimalMin(value = "0.1", message = "Las hectáreas mínimas deben ser mayor a 0.1")
    private Double hectareasMin;

    @DecimalMin(value = "0.1", message = "Las hectáreas máximas deben ser mayor a 0.1")
    private Double hectareasMax;

    @DecimalMin(value = "100.0", message = "El precio mínimo debe ser mayor a $100")
    private Double precioMin;

    @DecimalMin(value = "100.0", message = "El precio máximo debe ser mayor a $100")
    private Double precioMax;

    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;

    @Min(value = 1, message = "La cantidad mínima debe ser mayor a 0")
    private Integer cantidadMin;

    @Min(value = 1, message = "La cantidad máxima debe ser mayor a 0")
    private Integer cantidadMax;

    // Campos para ordenamiento
    private String ordenarPor = "nombre"; // nombre, fecha, precio, hectareas
    private String direccion = "ASC"; // ASC, DESC

    // Paginación
    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    private Integer pagina = 0;

    @Min(value = 1, message = "El tamaño debe ser mayor a 0")
    @Max(value = 100, message = "El tamaño no puede exceder 100")
    private Integer tamano = 10;

    // Constructores
    public BusquedaRequestDTO() {}

    public BusquedaRequestDTO(String nombre, String tipoCultivo) {
        this.nombre = nombre;
        this.tipoCultivo = tipoCultivo;
    }

    // Métodos de validación
    public boolean tieneRangoHectareasValido() {
        if (hectareasMin != null && hectareasMax != null) {
            return hectareasMin <= hectareasMax;
        }
        return true;
    }

    public boolean tieneRangoPrecioValido() {
        if (precioMin != null && precioMax != null) {
            return precioMin <= precioMax;
        }
        return true;
    }

    public boolean tieneRangoFechaValido() {
        if (fechaDesde != null && fechaHasta != null) {
            return !fechaDesde.isAfter(fechaHasta);
        }
        return true;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoCultivo() { return tipoCultivo; }
    public void setTipoCultivo(String tipoCultivo) { this.tipoCultivo = tipoCultivo; }

    public String getTemporada() { return temporada; }
    public void setTemporada(String temporada) { this.temporada = temporada; }

    public String getTipoSuelo() { return tipoSuelo; }
    public void setTipoSuelo(String tipoSuelo) { this.tipoSuelo = tipoSuelo; }

    public String getCodigoFinca() { return codigoFinca; }
    public void setCodigoFinca(String codigoFinca) { this.codigoFinca = codigoFinca; }

    public Double getHectareasMin() { return hectareasMin; }
    public void setHectareasMin(Double hectareasMin) { this.hectareasMin = hectareasMin; }

    public Double getHectareasMax() { return hectareasMax; }
    public void setHectareasMax(Double hectareasMax) { this.hectareasMax = hectareasMax; }

    public Double getPrecioMin() { return precioMin; }
    public void setPrecioMin(Double precioMin) { this.precioMin = precioMin; }

    public Double getPrecioMax() { return precioMax; }
    public void setPrecioMax(Double precioMax) { this.precioMax = precioMax; }

    public LocalDateTime getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDateTime fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDateTime getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDateTime fechaHasta) { this.fechaHasta = fechaHasta; }

    public Integer getCantidadMin() { return cantidadMin; }
    public void setCantidadMin(Integer cantidadMin) { this.cantidadMin = cantidadMin; }

    public Integer getCantidadMax() { return cantidadMax; }
    public void setCantidadMax(Integer cantidadMax) { this.cantidadMax = cantidadMax; } // ✅ CORREGIDO

    public String getOrdenarPor() { return ordenarPor; }
    public void setOrdenarPor(String ordenarPor) { this.ordenarPor = ordenarPor; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }

    public Integer getTamano() { return tamano; }
    public void setTamano(Integer tamano) { this.tamano = tamano; }

    @Override
    public String toString() {
        return String.format("BusquedaRequestDTO{nombre='%s', tipoCultivo='%s', pagina=%d, tamano=%d}",
                nombre, tipoCultivo, pagina, tamano);
    }
}