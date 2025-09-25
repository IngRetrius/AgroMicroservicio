package co.unibague.agropecuario.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de productos agrícolas
 * Versión simplificada del modelo para APIs externas
 */
public class ProductoAgricolaDTO {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "Las hectáreas cultivadas son obligatorias")
    @DecimalMin(value = "0.1", message = "Las hectáreas deben ser mayor a 0.1")
    @DecimalMax(value = "10000.0", message = "Las hectáreas no pueden exceder 10,000")
    @JsonProperty("hectareas")
    private Double hectareasCultivadas;

    @NotNull(message = "La cantidad producida es obligatoria")
    @Min(value = 1, message = "La cantidad producida debe ser mayor a 0")
    @JsonProperty("cantidad")
    private Integer cantidadProducida;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("fecha")
    private LocalDateTime fechaProduccion;

    @NotBlank(message = "El tipo de cultivo es obligatorio")
    @JsonProperty("tipo")
    private String tipoCultivo;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "100.0", message = "El precio debe ser mayor a $100")
    @JsonProperty("precio")
    private Double precioVenta;

    @JsonProperty("costo")
    private Double costoProduccion;

    private String temporada;
    private String tipoSuelo;
    private String codigoFinca;

    // Campos calculados (solo lectura)
    @JsonProperty("ingresoTotal")
    private Double ingresoTotal;

    @JsonProperty("rentabilidad")
    private Double rentabilidad;

    @JsonProperty("margenGanancia")
    private Double margenGanancia;

    @JsonProperty("rendimientoPorHa")
    private Double rendimientoPorHa;

    // Constructores
    public ProductoAgricolaDTO() {}

    public ProductoAgricolaDTO(String nombre, String tipoCultivo, Double hectareasCultivadas,
                               Integer cantidadProducida, Double precioVenta) {
        this.nombre = nombre;
        this.tipoCultivo = tipoCultivo;
        this.hectareasCultivadas = hectareasCultivadas;
        this.cantidadProducida = cantidadProducida;
        this.precioVenta = precioVenta;
        this.fechaProduccion = LocalDateTime.now();
    }

    // Métodos de cálculo
    public void calcularCamposDerivados() {
        if (cantidadProducida != null && precioVenta != null) {
            this.ingresoTotal = cantidadProducida * precioVenta;
        }

        if (hectareasCultivadas != null && hectareasCultivadas > 0 &&
                costoProduccion != null && ingresoTotal != null) {
            double costoTotal = costoProduccion * hectareasCultivadas;
            double utilidadNeta = ingresoTotal - costoTotal;
            this.rentabilidad = utilidadNeta / hectareasCultivadas;
        }

        if (costoProduccion != null && costoProduccion > 0 && precioVenta != null) {
            this.margenGanancia = ((precioVenta - costoProduccion) / costoProduccion) * 100;
        }

        if (cantidadProducida != null && hectareasCultivadas != null && hectareasCultivadas > 0) {
            this.rendimientoPorHa = cantidadProducida.doubleValue() / hectareasCultivadas;
        }
    }

    // Métodos de validación
    public boolean esValido() {
        return nombre != null && !nombre.trim().isEmpty() &&
                tipoCultivo != null && !tipoCultivo.trim().isEmpty() &&
                hectareasCultivadas != null && hectareasCultivadas > 0 &&
                cantidadProducida != null && cantidadProducida > 0 &&
                precioVenta != null && precioVenta > 0;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getHectareasCultivadas() { return hectareasCultivadas; }
    public void setHectareasCultivadas(Double hectareasCultivadas) { this.hectareasCultivadas = hectareasCultivadas; }

    public Integer getCantidadProducida() { return cantidadProducida; }
    public void setCantidadProducida(Integer cantidadProducida) { this.cantidadProducida = cantidadProducida; }

    public LocalDateTime getFechaProduccion() { return fechaProduccion; }
    public void setFechaProduccion(LocalDateTime fechaProduccion) { this.fechaProduccion = fechaProduccion; }

    public String getTipoCultivo() { return tipoCultivo; }
    public void setTipoCultivo(String tipoCultivo) { this.tipoCultivo = tipoCultivo; }

    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }

    public Double getCostoProduccion() { return costoProduccion; }
    public void setCostoProduccion(Double costoProduccion) { this.costoProduccion = costoProduccion; }

    public String getTemporada() { return temporada; }
    public void setTemporada(String temporada) { this.temporada = temporada; }

    public String getTipoSuelo() { return tipoSuelo; }
    public void setTipoSuelo(String tipoSuelo) { this.tipoSuelo = tipoSuelo; }

    public String getCodigoFinca() { return codigoFinca; }
    public void setCodigoFinca(String codigoFinca) { this.codigoFinca = codigoFinca; }

    public Double getIngresoTotal() { return ingresoTotal; }
    public void setIngresoTotal(Double ingresoTotal) { this.ingresoTotal = ingresoTotal; }

    public Double getRentabilidad() { return rentabilidad; }
    public void setRentabilidad(Double rentabilidad) { this.rentabilidad = rentabilidad; }

    public Double getMargenGanancia() { return margenGanancia; }
    public void setMargenGanancia(Double margenGanancia) { this.margenGanancia = margenGanancia; }

    public Double getRendimientoPorHa() { return rendimientoPorHa; }
    public void setRendimientoPorHa(Double rendimientoPorHa) { this.rendimientoPorHa = rendimientoPorHa; }

    @Override
    public String toString() {
        return String.format("ProductoAgricolaDTO{id='%s', nombre='%s', tipo='%s'}",
                id, nombre, tipoCultivo);
    }
}