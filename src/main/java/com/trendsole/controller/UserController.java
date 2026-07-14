package com.trendsole.controller;

import com.trendsole.model.User;
import com.trendsole.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

/**
 * UserController - REST API Controller for Users
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/users/profile → Get profile of currently logged-in user.
     */
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        String email = getAuthenticatedUserEmail();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * PUT /api/users/profile → Update fullName and phoneNumber for logged-in user.
     */
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody Map<String, String> request) {
        String email = getAuthenticatedUserEmail();
        String fullName = request.get("fullName");
        String phoneNumber = request.get("phoneNumber");

        User updatedUser = userService.updateProfile(email, fullName, phoneNumber);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * PUT /api/users/change-password → Change password for logged-in user.
     */
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        String email = getAuthenticatedUserEmail();
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        userService.changePassword(email, currentPassword, newPassword, confirmPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully!"));
    }

    private String getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalArgumentException("User is not authenticated. Please log in.");
        }
        return auth.getName();
    }

    /**
     * POST /api/users/register → Register a new user.
     * HTTP Method: POST
     * Example: http://localhost:8080/api/users/register
     *
     * Request Body (JSON):
     * {
     *   "fullName": "Naman Tripathi",
     *   "email": "naman@example.com",
     *   "password": "myPassword123",
     *   "phoneNumber": "+91 98765 43210"
     * }
     *
     * Note: role and createdAt are set automatically by the service layer.
     *
     * Returns: The created user with HTTP 201 (Created) status.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    /**
     * GET /api/users → Get all users.
     * HTTP Method: GET
     * Example: http://localhost:8080/api/users
     * Returns: List of all users in JSON format.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * GET /api/users/{id} → Get a single user by ID.
     * HTTP Method: GET
     * Example: http://localhost:8080/api/users/1
     * @PathVariable → Extracts the {id} from the URL.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);  // Returns 200 OK with user data
    }

    /**
     * GET /api/users/email/{email} → Get a user by email.
     * HTTP Method: GET
     * Example: http://localhost:8080/api/users/email/naman@example.com
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * DELETE /api/users/{id} → Delete a user.
     * HTTP Method: DELETE
     * Example: http://localhost:8080/api/users/1
     * Returns: A success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    /**
     * Exception handler for IllegalArgumentException.
     * Returns a 400 Bad Request status with the exception message as the body.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * General exception handler for any unhandled registration/user error.
     * Returns a 400 Bad Request status with the exact exception message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "Registration error: " + ex.getClass().getSimpleName();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
}
