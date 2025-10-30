package co.unibague.agropecuario.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidad JPA Cosecha - Detalle de ProductoAgricola
 * Relación: ProductoAgricola (1) ---> (0..*) Cosecha
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 * Tercer Prototipo - Persistencia con MySQL
 */
@Entity
@Table(name = "cosecha")
public class Cosecha {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    @Size(max = 10, message = "El ID no puede exceder 10 caracteres")
    private String id;

    // Relación Many-to-One con ProductoAgricola (sin usar listas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cosecha_producto"))
    @JsonIgnore
    private ProductoAgricola producto;

    // Campo para compatibilidad con API REST (setter/getter manual)
    @Transient
    @NotBlank(message = "El ID del producto es obligatorio")
    @JsonProperty("productoId")
    private String productoId;

    @Column(name = "fecha_cosecha", nullable = false)
    @NotNull(message = "La fecha de cosecha es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("fechaCosecha")
    private LocalDateTime fechaCosecha;

    @Column(name = "cantidad_recolectada", nullable = false)
    @NotNull(message = "La cantidad recolectada es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @JsonProperty("cantidadRecolectada")
    private Integer cantidadRecolectada; // en kg

    @Column(name = "calidad_producto", length = 30, nullable = false)
    @NotBlank(message = "La calidad del producto es obligatoria")
    @JsonProperty("calidadProducto")
    private String calidadProducto; // Premium, Extra, Estándar, Segunda

    @Column(name = "numero_trabajadores")
    @Min(value = 1, message = "El número de trabajadores debe ser mayor a 0")
    @JsonProperty("numeroTrabajadores")
    private Integer numeroTrabajadores;

    @Column(name = "costo_mano_obra")
    @DecimalMin(value = "0.0", message = "El costo debe ser mayor o igual a 0")
    @JsonProperty("costoManoObra")
    private Double costoManoObra;

    @Column(name = "condiciones_climaticas", length = 50)
    @JsonProperty("condicionesClimaticas")
    private String condicionesClimaticas; // Soleado, Lluvioso, Nublado, etc.

    @Column(name = "estado_cosecha", length = 30)
    @JsonProperty("estadoCosecha")
    private String estadoCosecha; // Completada, En proceso, Pendiente

    @Column(name = "observaciones", length = 500)
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

    // Getter para productoId (obtiene el ID del producto relacionado)
    public String getProductoId() {
        if (producto != null) {
            return producto.getId();
        }
        return productoId;
    }

    // Setter para productoId (almacena temporalmente, se debe sincronizar con producto)
    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    // Getter/Setter para la entidad ProductoAgricola
    public ProductoAgricola getProducto() {
        return producto;
    }

    public void setProducto(ProductoAgricola producto) {
        this.producto = producto;
        if (producto != null) {
            this.productoId = producto.getId();
        }
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