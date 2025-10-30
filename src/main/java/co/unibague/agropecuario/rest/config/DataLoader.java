package co.unibague.agropecuario.rest.config;

import co.unibague.agropecuario.rest.model.Cosecha;
import co.unibague.agropecuario.rest.model.ProductoAgricola;
import co.unibague.agropecuario.rest.repository.CosechaJpaRepository;
import co.unibague.agropecuario.rest.repository.ProductoAgricolaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

/**
 * Configuración para cargar datos iniciales en la base de datos
 * Solo se ejecuta en el perfil 'dev' para desarrollo
 * Universidad de Ibagué - Desarrollo de Aplicaciones Empresariales
 */
@Configuration
@Profile("dev")  // Solo se ejecuta en entorno de desarrollo
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(
            @Autowired ProductoAgricolaJpaRepository productoRepository,
            @Autowired CosechaJpaRepository cosechaRepository) {

        return args -> {
            // Verificar si ya hay datos en la base de datos
            if (productoRepository.count() > 0) {
                logger.info("La base de datos ya contiene datos. Se omite la carga inicial.");
                return;
            }

            logger.info("=== INICIANDO CARGA DE DATOS DE PRUEBA ===");

            // ================================================================
            // CREAR PRODUCTOS AGRÍCOLAS
            // ================================================================

            ProductoAgricola cafe = new ProductoAgricola();
            cafe.setId("AGR001");
            cafe.setNombre("Café Premium Colombiano");
            cafe.setHectareasCultivadas(50.5);
            cafe.setCantidadProducida(12000);
            cafe.setFechaProduccion(LocalDateTime.of(2025, 1, 15, 8, 30));
            cafe.setTipoCultivo("Café");
            cafe.setPrecioVenta(8500.00);
            cafe.setCostoProduccion(5200.00);
            cafe.setRendimientoPorHa(237.62);
            cafe.setTemporada("Todo el año");
            cafe.setTipoSuelo("Franco arcilloso");
            cafe.setCodigoFinca("FNC-001");

            ProductoAgricola arroz = new ProductoAgricola();
            arroz.setId("AGR002");
            arroz.setNombre("Arroz Orgánico de Primera");
            arroz.setHectareasCultivadas(120.0);
            arroz.setCantidadProducida(45000);
            arroz.setFechaProduccion(LocalDateTime.of(2025, 2, 10, 10, 0));
            arroz.setTipoCultivo("Arroz");
            arroz.setPrecioVenta(3200.00);
            arroz.setCostoProduccion(2100.00);
            arroz.setRendimientoPorHa(375.00);
            arroz.setTemporada("Lluviosa");
            arroz.setTipoSuelo("Franco limoso");
            arroz.setCodigoFinca("ARR-002");

            ProductoAgricola cacao = new ProductoAgricola();
            cacao.setId("AGR003");
            cacao.setNombre("Cacao Fino de Aroma");
            cacao.setHectareasCultivadas(35.8);
            cacao.setCantidadProducida(8500);
            cacao.setFechaProduccion(LocalDateTime.of(2025, 3, 5, 9, 15));
            cacao.setTipoCultivo("Cacao");
            cacao.setPrecioVenta(12000.00);
            cacao.setCostoProduccion(7500.00);
            cacao.setRendimientoPorHa(237.43);
            cacao.setTemporada("Todo el año");
            cacao.setTipoSuelo("Franco");
            cacao.setCodigoFinca("CAC-003");

            // Guardar productos
            productoRepository.save(cafe);
            productoRepository.save(arroz);
            productoRepository.save(cacao);

            logger.info("✓ 3 productos agrícolas creados");

            // ================================================================
            // CREAR COSECHAS (con relación @ManyToOne)
            // ================================================================

            // Cosecha 1: Café Premium - Cosecha 1
            Cosecha cos1 = new Cosecha();
            cos1.setId("COS001");
            cos1.setProducto(cafe);  // Relación @ManyToOne
            cos1.setFechaCosecha(LocalDateTime.of(2025, 4, 10, 7, 0));
            cos1.setCantidadRecolectada(2800);
            cos1.setCalidadProducto("Premium");
            cos1.setNumeroTrabajadores(15);
            cos1.setCostoManoObra(450000.00);
            cos1.setCondicionesClimaticas("Soleado");
            cos1.setEstadoCosecha("Completada");
            cos1.setObservaciones("Excelente calidad, granos grandes y uniformes");

            // Cosecha 2: Café Premium - Cosecha 2
            Cosecha cos2 = new Cosecha();
            cos2.setId("COS002");
            cos2.setProducto(cafe);  // Relación @ManyToOne
            cos2.setFechaCosecha(LocalDateTime.of(2025, 5, 15, 6, 30));
            cos2.setCantidadRecolectada(3200);
            cos2.setCalidadProducto("Extra");
            cos2.setNumeroTrabajadores(18);
            cos2.setCostoManoObra(540000.00);
            cos2.setCondicionesClimaticas("Parcialmente nublado");
            cos2.setEstadoCosecha("Completada");
            cos2.setObservaciones("Buena producción, algunos granos de menor tamaño");

            // Cosecha 3: Arroz Orgánico - Cosecha 1
            Cosecha cos3 = new Cosecha();
            cos3.setId("COS003");
            cos3.setProducto(arroz);  // Relación @ManyToOne
            cos3.setFechaCosecha(LocalDateTime.of(2025, 6, 1, 8, 0));
            cos3.setCantidadRecolectada(15000);
            cos3.setCalidadProducto("Estándar");
            cos3.setNumeroTrabajadores(25);
            cos3.setCostoManoObra(750000.00);
            cos3.setCondicionesClimaticas("Lluvioso");
            cos3.setEstadoCosecha("Completada");
            cos3.setObservaciones("Cosecha en temporada de lluvias, rendimiento esperado");

            // Cosecha 4: Arroz Orgánico - Cosecha 2
            Cosecha cos4 = new Cosecha();
            cos4.setId("COS004");
            cos4.setProducto(arroz);  // Relación @ManyToOne
            cos4.setFechaCosecha(LocalDateTime.of(2025, 7, 20, 9, 0));
            cos4.setCantidadRecolectada(18000);
            cos4.setCalidadProducto("Premium");
            cos4.setNumeroTrabajadores(30);
            cos4.setCostoManoObra(900000.00);
            cos4.setCondicionesClimaticas("Soleado");
            cos4.setEstadoCosecha("Completada");
            cos4.setObservaciones("Excelente calidad orgánica certificada");

            // Cosecha 5: Cacao Fino - Cosecha 1
            Cosecha cos5 = new Cosecha();
            cos5.setId("COS005");
            cos5.setProducto(cacao);  // Relación @ManyToOne
            cos5.setFechaCosecha(LocalDateTime.of(2025, 8, 12, 10, 0));
            cos5.setCantidadRecolectada(2500);
            cos5.setCalidadProducto("Premium");
            cos5.setNumeroTrabajadores(12);
            cos5.setCostoManoObra(360000.00);
            cos5.setCondicionesClimaticas("Soleado");
            cos5.setEstadoCosecha("Completada");
            cos5.setObservaciones("Cacao de aroma excepcional, fermentación perfecta");

            // Guardar cosechas
            cosechaRepository.save(cos1);
            cosechaRepository.save(cos2);
            cosechaRepository.save(cos3);
            cosechaRepository.save(cos4);
            cosechaRepository.save(cos5);

            logger.info("✓ 5 cosechas creadas");

            // ================================================================
            // VERIFICACIÓN
            // ================================================================

            long totalProductos = productoRepository.count();
            long totalCosechas = cosechaRepository.count();

            logger.info("=== DATOS INICIALES CARGADOS EXITOSAMENTE ===");
            logger.info("Total productos: {}", totalProductos);
            logger.info("Total cosechas: {}", totalCosechas);

            // Verificar relación maestro-detalle
            productoRepository.findAll().forEach(p -> {
                long cosechasDelProducto = cosechaRepository.countByProducto(p);
                logger.info("Producto {} tiene {} cosechas", p.getNombre(), cosechasDelProducto);
            });
        };
    }
}
