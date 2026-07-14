package com.trendsole.config;

import com.trendsole.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Security Configuration for the Application
 *
 * What it does:
 * - Provides a BCryptPasswordEncoder bean for hashing user passwords.
 * - Configures session-based authentication using Spring Security.
 * - Disables CSRF protection (not needed for REST APIs with fetch calls).
 * - Defines which endpoints are public and which require authentication.
 * - Provides an AuthenticationManager bean for manual authentication in AuthController.
 *
 * Session-based auth flow:
 * 1. User sends POST /api/auth/login with email + password.
 * 2. AuthController uses AuthenticationManager to verify credentials.
 * 3. On success, Spring Security stores auth in the HTTP session (JSESSIONID cookie).
 * 4. All subsequent requests include the session cookie automatically.
 * 5. On logout, the session is invalidated.
 *
 * @Configuration → Tells Spring this class contains configuration settings.
 * @EnableWebSecurity → Enables Spring Security's web security support.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

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
     * Authentication Provider
     *
     * This connects our CustomUserDetailsService with BCryptPasswordEncoder.
     * When Spring Security authenticates a user:
     * 1. It calls userDetailsService.loadUserByUsername(email) to get the stored hash.
     * 2. It uses passwordEncoder to compare the provided password with the stored hash.
     * 3. If they match → authentication succeeds.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager Bean
     *
     * This is needed by AuthController to manually authenticate users.
     * Without this bean, we cannot call authenticationManager.authenticate(...) in our controller.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security Filter Chain
     *
     * This configures HTTP security for the application:
     * - CSRF is disabled (REST APIs use JSON, not form submissions).
     * - Public endpoints: all static pages, product/cart/order APIs, auth APIs, registration.
     * - Protected endpoints: /api/users (admin-level user management).
     * - Session management: sessions are created when needed (login).
     *
     * Note: All existing pages and features remain publicly accessible.
     * Only the /api/users endpoint (list/delete users) requires authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST APIs (we use fetch with JSON, not HTML forms)
            .csrf(csrf -> csrf.disable())

            // Configure URL-based authorization (top-to-bottom evaluation)
            .authorizeHttpRequests(auth -> auth
                // 1. Registration endpoint — MUST be permitted FIRST
                .requestMatchers("/api/users/register", "/api/users/register/**").permitAll()

                // 2. Authentication endpoints — always public (login, logout, session check)
                .requestMatchers("/api/auth/**").permitAll()

                // 3. Authenticated profile and address endpoints
                .requestMatchers("/api/users/profile", "/api/users/change-password").authenticated()
                .requestMatchers("/api/addresses", "/api/addresses/**").authenticated()

                // 4. User administration endpoints — require authentication
                .requestMatchers("/api/users", "/api/users/**").authenticated()

                // 5. All other static pages, assets, products, cart, and orders — public
                .anyRequest().permitAll()
            )

            // Use our custom authentication provider
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
