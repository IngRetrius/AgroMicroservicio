package co.unibague.agropecuario.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProductoAgricola {

    @NotBlank(message = "El ID es obligatorio")
    @Size(min = 3, max = 10, message = "El ID debe tener entre 3 y 10 caracteres")
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "Las hectáreas cultivadas son obligatorias")
    @DecimalMin(value = "0.1", message = "Las hectáreas deben ser mayor a 0.1")
    @DecimalMax(value = "10000.0", message = "Las hectáreas no pueden exceder 10,000")
    @JsonProperty("hectareasCultivadas")
    private Double hectareasCultivadas;

    @NotNull(message = "La cantidad producida es obligatoria")
    @Min(value = 1, message = "La cantidad producida debe ser mayor a 0")
    @Max(value = 1000000, message = "La cantidad producida no puede exceder 1,000,000")
    @JsonProperty("cantidadProducida")
    private Integer cantidadProducida;

    @NotNull(message = "La fecha de producción es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("fechaProduccion")
    private LocalDateTime fechaProduccion;

    @NotBlank(message = "El tipo de cultivo es obligatorio")
    @JsonProperty("tipoCultivo")
    private String tipoCultivo;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "100.0", message = "El precio debe ser mayor a $100")
    @DecimalMax(value = "1000000.0", message = "El precio no puede exceder $1,000,000")
    @JsonProperty("precioVenta")
    private Double precioVenta;

    @NotNull(message = "El costo de producción es obligatorio")
    @DecimalMin(value = "100.0", message = "El costo debe ser mayor a $100")
    @JsonProperty("costoProduccion")
    private Double costoProduccion;

    @JsonProperty("rendimientoPorHa")
    private Double rendimientoPorHa;

    private String temporada;

    @JsonProperty("tipoSuelo")
    private String tipoSuelo;

    @JsonProperty("codigoFinca")
    private String codigoFinca;

    // Constructores
    public ProductoAgricola() {
        this.fechaProduccion = LocalDateTime.now();
        this.temporada = "Todo el año";
        this.tipoSuelo = "Franco";
    }

    public ProductoAgricola(String id, String nombre, Double hectareasCultivadas,
                            Integer cantidadProducida, LocalDateTime fechaProduccion,
                            String tipoCultivo, Double precioVenta, Double costoProduccion) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.hectareasCultivadas = hectareasCultivadas;
        this.cantidadProducida = cantidadProducida;
        this.fechaProduccion = fechaProduccion;
        this.tipoCultivo = tipoCultivo;
        this.precioVenta = precioVenta;
        this.costoProduccion = costoProduccion;
    }

    // Métodos de negocio
    public double calcularRentabilidad() {
        if (hectareasCultivadas == null || hectareasCultivadas == 0) return 0;

        double ingresoTotal = calcularIngresoTotal();
        double costoTotal = costoProduccion * hectareasCultivadas;
        double utilidadNeta = ingresoTotal - costoTotal;

        return utilidadNeta / hectareasCultivadas;
    }

    public double calcularIngresoTotal() {
        if (cantidadProducida == null || precioVenta == null) return 0;
        return cantidadProducida * precioVenta;
    }

    public double calcularMargenGanancia() {
        if (costoProduccion == null || costoProduccion == 0) return 0;
        return ((precioVenta - costoProduccion) / costoProduccion) * 100;
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

    public Double getRendimientoPorHa() { return rendimientoPorHa; }
    public void setRendimientoPorHa(Double rendimientoPorHa) { this.rendimientoPorHa = rendimientoPorHa; }

    public String getTemporada() { return temporada; }
    public void setTemporada(String temporada) { this.temporada = temporada; }

    public String getTipoSuelo() { return tipoSuelo; }
    public void setTipoSuelo(String tipoSuelo) { this.tipoSuelo = tipoSuelo; }

    public String getCodigoFinca() { return codigoFinca; }
    public void setCodigoFinca(String codigoFinca) { this.codigoFinca = codigoFinca; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductoAgricola that = (ProductoAgricola) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ProductoAgricola{id='%s', nombre='%s', tipo='%s', hectareas=%.2f}",
                id, nombre, tipoCultivo, hectareasCultivadas);
    }
}