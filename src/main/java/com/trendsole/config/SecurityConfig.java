package com.trendsole.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Security Configuration for the Application
 *
 * What it does:
 * - Provides a BCryptPasswordEncoder bean for hashing user passwords.
 * - Disables the default Spring Security login page and CSRF protection.
 * - Allows ALL API endpoints to be accessed without authentication.
 *
 * Why disable security?
 * - Login/authentication is NOT implemented yet.
 * - We only need Spring Security right now for the BCryptPasswordEncoder.
 * - When we implement login later, we will update this configuration
 *   to protect specific endpoints.
 *
 * @Configuration → Tells Spring this class contains configuration settings.
 * @EnableWebSecurity → Enables Spring Security's web security support.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCryptPasswordEncoder Bean
     *
     * What is BCrypt?
     * - BCrypt is a password-hashing algorithm.
     * - It converts plain text passwords into secure hashes that CANNOT be reversed.
     * - Example: "myPassword123" → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     *
     * Why use it?
     * - NEVER store passwords as plain text in the database.
     * - Even if the database is hacked, the passwords remain safe.
     *
     * @Bean → Tells Spring to create and manage this object so it can be @Autowired anywhere.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security Filter Chain
     *
     * This configures HTTP security for the application:
     * - csrf().disable()         → Disables CSRF protection (not needed for REST APIs).
     * - authorizeHttpRequests()  → Allows ALL requests without authentication.
     *
     * Note: This is a permissive configuration for development.
     * When login is implemented, we will restrict access to specific endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                          // Disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()                           // Allow all requests without login
            );

        return http.build();
    }
}
