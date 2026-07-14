package com.trendsole.controller;

import com.trendsole.model.User;
import com.trendsole.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthController — Handles Login, Logout, and Session Status.
 *
 * What is this?
 * - This controller manages user authentication (login/logout).
 * - It does NOT handle registration — that stays in UserController.
 * - It uses Spring Security's AuthenticationManager to verify credentials.
 *
 * Endpoints:
 * - POST /api/auth/login   → Log in with email + password
 * - POST /api/auth/logout  → Log out and invalidate session
 * - GET  /api/auth/me      → Get currently logged-in user info
 *
 * Session-based auth:
 * - On successful login, Spring Security stores the authentication in the HTTP session.
 * - The session cookie (JSESSIONID) is automatically sent with every subsequent request.
 * - No JWT tokens are used — this is traditional session-based authentication.
 *
 * @RestController → Returns JSON responses.
 * @RequestMapping("/api/auth") → Base URL: http://localhost:8080/api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    /**
     * POST /api/auth/login → Authenticate a user.
     *
     * Request Body (JSON):
     * {
     *   "email": "naman@example.com",
     *   "password": "myPassword123"
     * }
     *
     * On success: Returns user info (fullName, email, role) + HTTP 200.
     * On failure: Returns generic error message + HTTP 401.
     *
     * Security Note:
     * - The error message is intentionally vague ("Invalid email or password").
     * - We NEVER reveal whether the email exists or the password was wrong.
     * - This prevents attackers from discovering valid email addresses.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {
            // Authenticate using Spring Security's AuthenticationManager
            // This internally calls CustomUserDetailsService.loadUserByUsername()
            // and compares the password using BCryptPasswordEncoder
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Store authentication in the security context (session)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create a new session to prevent session fixation attacks
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Look up the full user object to return their info
            User user = userRepository.findByEmail(email).orElse(null);

            // Build response with user info (never include password)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("fullName", user != null ? user.getFullName() : "");
            response.put("email", email);
            response.put("role", user != null ? user.getRole().name() : "USER");

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // Generic error — don't reveal whether email or password was wrong
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * POST /api/auth/logout → Log out the current user.
     *
     * What happens:
     * 1. The HTTP session is invalidated (deleted from the server).
     * 2. The security context is cleared.
     * 3. The JSESSIONID cookie becomes invalid.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Invalidate the current session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear the security context
        SecurityContextHolder.clearContext();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/me → Get the currently logged-in user's information.
     *
     * Used by the frontend on every page load to check:
     * - Is the user logged in? → Show "My Profile / Logout" in navbar
     * - Is the user a guest?   → Show "Login / Register" in navbar
     *
     * Returns 200 with user info if authenticated, or 401 if not.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated (not anonymous)
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        // Get the email from the authentication principal
        String email = authentication.getName();

        // Look up full user info
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not found"));
        }

        // Return user info (never include password — @JsonProperty WRITE_ONLY handles this)
        Map<String, Object> response = new HashMap<>();
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }
}
