package co.unibague.agropecuario.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${cors.max-age}")
    private long maxAge;

    @Value("${cors.api-path}")
    private String apiPath;

    @Value("${cors.actuator-path}")
    private String actuatorPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // API endpoints CORS configuration
        registry.addMapping(apiPath)
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .exposedHeaders(exposedHeaders.split(","))
                .maxAge(maxAge);

        // Actuator endpoints CORS configuration
        registry.addMapping(actuatorPath)
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods("GET", "POST")
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
}