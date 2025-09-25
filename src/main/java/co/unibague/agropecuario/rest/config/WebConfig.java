package co.unibague.agropecuario.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(false) //
                .exposedHeaders("Authorization", "Content-Type")
                .maxAge(3600);

        registry.addMapping("/actuator/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(false) // ←
                .maxAge(3600);
    }
}