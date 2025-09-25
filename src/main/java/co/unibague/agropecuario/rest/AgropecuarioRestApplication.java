package co.unibague.agropecuario.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AgropecuarioRestApplication {

    public static void main(String[] args) {
        System.setProperty("user.timezone", "America/Bogota");
        System.setProperty("spring.application.name", "Agropecuario REST API");
        System.setProperty("file.encoding", "UTF-8");

        var context = SpringApplication.run(AgropecuarioRestApplication.class, args);
        mostrarInformacionInicio(context);
    }

    @PostConstruct
    public void configurarZonaHoraria() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        System.out.println("Zona horaria configurada: " + TimeZone.getDefault().getID());
    }

    private static void mostrarInformacionInicio(org.springframework.context.ConfigurableApplicationContext context) {
        String puerto = context.getEnvironment().getProperty("server.port", "8081");
        String contextPath = context.getEnvironment().getProperty("server.servlet.context-path", "");
        String perfil = String.join(", ", context.getEnvironment().getActiveProfiles());

        System.out.println("\n" + "=".repeat(80));
        System.out.println("AGROPECUARIO REST API - INICIADA EXITOSAMENTE");
        System.out.println("=".repeat(80));
        System.out.println("Universidad de Ibagué - Tolima, Colombia");
        System.out.println("URL Local: http://localhost:" + puerto + contextPath);
        System.out.println("API Endpoints: http://localhost:" + puerto + contextPath + "/api/productos");
        System.out.println("Health Check: http://localhost:" + puerto + contextPath + "/actuator/health");
        System.out.println("Perfil Activo: " + (perfil.isEmpty() ? "default" : perfil));
        System.out.println("Zona Horaria: America/Bogota");
        System.out.println("Tiempo de inicio: " + java.time.LocalDateTime.now());
        System.out.println("=".repeat(80));
        System.out.println("La aplicación está lista para recibir peticiones");
        System.out.println("=".repeat(80) + "\n");
    }
}