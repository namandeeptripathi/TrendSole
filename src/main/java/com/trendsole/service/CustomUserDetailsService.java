package com.trendsole.service;

import com.trendsole.model.User;
import com.trendsole.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService — Bridges our User entity with Spring Security.
 *
 * What is this?
 * - Spring Security needs a way to load user data during authentication.
 * - It uses the UserDetailsService interface for this purpose.
 * - This class implements that interface and tells Spring Security:
 *   "Here's how to find a user by their email and get their password + role."
 *
 * How it works:
 * 1. When a user tries to log in, Spring Security calls loadUserByUsername().
 * 2. We look up the user in our database using UserRepository.findByEmail().
 * 3. We return a UserDetails object containing the email, hashed password, and role.
 * 4. Spring Security then compares the provided password with the stored hash using BCrypt.
 *
 * Note: The method is called "loadUserByUsername" but we use EMAIL as the username.
 *
 * @Service → Tells Spring to manage this class as a service bean.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load a user by their email address (used as "username" in Spring Security).
     *
     * @param email The email entered during login.
     * @return UserDetails object that Spring Security uses for authentication.
     * @throws UsernameNotFoundException if no user is found with that email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user in database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Build and return a Spring Security UserDetails object
        // - email is used as the "username"
        // - password is the BCrypt hash stored in the database
        // - role is prefixed with "ROLE_" as required by Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name()) // e.g., "USER" → authority "ROLE_USER"
                .build();
    }
}
