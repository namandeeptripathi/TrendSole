package com.trendsole.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig - Cross-Origin Resource Sharing Configuration
 *
 * What is CORS?
 * - CORS is a security feature in browsers.
 * - By default, a browser blocks requests from one URL to another URL
 *   if they are on different ports or domains.
 * - Example: Frontend on http://localhost:5500 cannot call Backend on http://localhost:8080
 *   unless we explicitly allow it.
 *
 * What this class does:
 * - It tells Spring Boot: "Allow requests from ANY origin (frontend) to our API."
 * - This way, our HTML/JS frontend can call our REST APIs without being blocked.
 *
 * @Configuration → Tells Spring this class contains configuration settings.
 * @Bean → Tells Spring to create and manage this object.
 */
@Configuration
public class CorsConfig {

    /**
     * This method configures CORS rules for the entire application.
     *
     * addMapping("/**")      → Apply CORS to ALL API endpoints.
     * allowedOrigins("*")    → Allow requests from ANY origin (any frontend URL).
     * allowedMethods(...)    → Allow these HTTP methods: GET, POST, PUT, DELETE.
     * allowedHeaders("*")    → Allow any headers in the request.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")            // Apply to all endpoints
                        .allowedOrigins("*")           // Allow all origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allowed HTTP methods
                        .allowedHeaders("*");          // Allow all headers
            }
        };
    }
}
