package co.unibague.agropecuario.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Modelo de Cosecha - Detalle de ProductoAgricola
 * Relación: ProductoAgricola (1) ---> (0..*) Cosecha
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
public class Cosecha {

    @Size(max = 10, message = "El ID no puede exceder 10 caracteres")
    private String id;

    @NotBlank(message = "El ID del producto es obligatorio")
    @JsonProperty("productoId")
    private String productoId; // ⚠️ FK a ProductoAgricola

    @NotNull(message = "La fecha de cosecha es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("fechaCosecha")
    private LocalDateTime fechaCosecha;

    @NotNull(message = "La cantidad recolectada es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @JsonProperty("cantidadRecolectada")
    private Integer cantidadRecolectada; // en kg

    @NotBlank(message = "La calidad del producto es obligatoria")
    @JsonProperty("calidadProducto")
    private String calidadProducto; // Premium, Extra, Estándar, Segunda

    @Min(value = 1, message = "El número de trabajadores debe ser mayor a 0")
    @JsonProperty("numeroTrabajadores")
    private Integer numeroTrabajadores;

    @DecimalMin(value = "0.0", message = "El costo debe ser mayor o igual a 0")
    @JsonProperty("costoManoObra")
    private Double costoManoObra;

    @JsonProperty("condicionesClimaticas")
    private String condicionesClimaticas; // Soleado, Lluvioso, Nublado, etc.

    @JsonProperty("estadoCosecha")
    private String estadoCosecha; // Completada, En proceso, Pendiente

    @JsonProperty("observaciones")
    private String observaciones;

    // ===== CONSTRUCTORES =====

    public Cosecha() {
        this.fechaCosecha = LocalDateTime.now();
        this.estadoCosecha = "Pendiente";
        this.condicionesClimaticas = "Normal";
    }

    public Cosecha(String id, String productoId, LocalDateTime fechaCosecha,
                   Integer cantidadRecolectada, String calidadProducto) {
        this();
        this.id = id;
        this.productoId = productoId;
        this.fechaCosecha = fechaCosecha;
        this.cantidadRecolectada = cantidadRecolectada;
        this.calidadProducto = calidadProducto;
    }

    public Cosecha(String productoId, LocalDateTime fechaCosecha, Integer cantidadRecolectada) {
        this();
        this.productoId = productoId;
        this.fechaCosecha = fechaCosecha;
        this.cantidadRecolectada = cantidadRecolectada;
        this.calidadProducto = "Estándar";
    }

    // ===== MÉTODOS DE NEGOCIO =====

    /**
     * Calcula el costo por kilogramo recolectado
     */
    public double calcularCostoPorKg() {
        if (cantidadRecolectada == null || cantidadRecolectada == 0) {
            return 0.0;
        }
        if (costoManoObra == null) {
            return 0.0;
        }
        return costoManoObra / cantidadRecolectada;
    }

    /**
     * Calcula el costo por trabajador
     */
    public double calcularCostoPorTrabajador() {
        if (numeroTrabajadores == null || numeroTrabajadores == 0) {
            return 0.0;
        }
        if (costoManoObra == null) {
            return 0.0;
        }
        return costoManoObra / numeroTrabajadores;
    }

    /**
     * Verifica si la cosecha es de calidad premium
     */
    public boolean esCalidadPremium() {
        return calidadProducto != null &&
                calidadProducto.equalsIgnoreCase("Premium");
    }

    /**
     * Calcula el rendimiento (kg por trabajador)
     */
    public double calcularRendimiento() {
        if (numeroTrabajadores == null || numeroTrabajadores == 0) {
            return 0.0;
        }
        if (cantidadRecolectada == null) {
            return 0.0;
        }
        return (double) cantidadRecolectada / numeroTrabajadores;
    }

    /**
     * Verifica si la cosecha es reciente (últimos 30 días)
     */
    public boolean esCosechaReciente() {
        if (fechaCosecha == null) {
            return false;
        }
        LocalDateTime hace30Dias = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        return fechaCosecha.isAfter(hace30Dias);
    }

    /**
     * Obtiene el número de días desde la cosecha
     */
    public long getDiasDesdeCoecha() {
        if (fechaCosecha == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaCosecha, LocalDateTime.now());
    }

    // ===== GETTERS Y SETTERS =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public LocalDateTime getFechaCosecha() {
        return fechaCosecha;
    }

    public void setFechaCosecha(LocalDateTime fechaCosecha) {
        this.fechaCosecha = fechaCosecha;
    }

    public Integer getCantidadRecolectada() {
        return cantidadRecolectada;
    }

    public void setCantidadRecolectada(Integer cantidadRecolectada) {
        this.cantidadRecolectada = cantidadRecolectada;
    }

    public String getCalidadProducto() {
        return calidadProducto;
    }

    public void setCalidadProducto(String calidadProducto) {
        this.calidadProducto = calidadProducto;
    }

    public Integer getNumeroTrabajadores() {
        return numeroTrabajadores;
    }

    public void setNumeroTrabajadores(Integer numeroTrabajadores) {
        this.numeroTrabajadores = numeroTrabajadores;
    }

    public Double getCostoManoObra() {
        return costoManoObra;
    }

    public void setCostoManoObra(Double costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    public String getCondicionesClimaticas() {
        return condicionesClimaticas;
    }

    public void setCondicionesClimaticas(String condicionesClimaticas) {
        this.condicionesClimaticas = condicionesClimaticas;
    }

    public String getEstadoCosecha() {
        return estadoCosecha;
    }

    public void setEstadoCosecha(String estadoCosecha) {
        this.estadoCosecha = estadoCosecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // ===== MÉTODOS ESTÁNDAR =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cosecha cosecha = (Cosecha) obj;
        return Objects.equals(id, cosecha.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Cosecha{id='%s', productoId='%s', fecha=%s, cantidad=%d kg, calidad='%s'}",
                id, productoId,
                fechaCosecha != null ? fechaCosecha.toLocalDate() : "null",
                cantidadRecolectada != null ? cantidadRecolectada : 0,
                calidadProducto);
    }
}