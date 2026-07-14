package com.trendsole.service;

import com.trendsole.model.Role;
import com.trendsole.model.User;
import com.trendsole.repository.UserRepository;
import com.trendsole.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserService - Business Logic Layer for Users
 *
 * What is this?
 * - The Service layer sits BETWEEN the Controller and the Repository.
 * - Controller → receives the request from the user.
 * - Service    → processes the business logic (hashing passwords, setting defaults).
 * - Repository → talks to the database.
 *
 * Why do we need it?
 * - Keeps the Controller clean (Controller should only handle requests).
 * - All the "thinking" / logic happens here.
 * - Passwords are hashed HERE before saving, never in the Controller.
 *
 * @Service → Tells Spring "this class contains business logic".
 * @Autowired → Spring automatically creates and injects dependencies.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Register a new user.
     *
     * Steps:
     * 1. Check if the email is already taken.
     * 2. Hash the password using BCrypt (never store plain text passwords!).
     * 3. Set the default role to USER.
     * 4. Set the createdAt timestamp to the current time.
     * 5. Save the user to the database.
     *
     * @param user The user object from the registration request.
     * @return The saved user with generated ID.
     * @throws IllegalArgumentException if the email is already registered.
     */
    public User registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Registration request cannot be empty.");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        // Step 1: Check if email is already taken
        if (userRepository.existsByEmail(user.getEmail().trim())) {
            throw new IllegalArgumentException("Email is already registered: " + user.getEmail());
        }
        user.setEmail(user.getEmail().trim());
        user.setFullName(user.getFullName().trim());

        // Step 2: Hash the password using BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Step 3: Set default role to USER
        user.setRole(Role.USER);

        // Step 4: Set the account creation timestamp
        user.setCreatedAt(LocalDateTime.now());

        // Step 5: Save to database and return
        return userRepository.save(user);
    }

    /**
     * Get ALL users from the database.
     * Calls: SELECT * FROM users
     *
     * Note: In a production app, you would NOT return passwords.
     * This is a basic implementation — we will add DTOs later.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get a SINGLE user by their ID.
     * If the user is not found, throw an error with a helpful message.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Get a user by their email address.
     * Useful for looking up accounts.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Update profile details (fullName and phoneNumber) for a user.
     * Note: Email cannot be changed.
     */
    public User updateProfile(String email, String fullName, String phoneNumber) {
        User user = getUserByEmail(email);

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }

        user.setFullName(fullName.trim());
        user.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);

        return userRepository.save(user);
    }

    /**
     * Change password for the logged-in user.
     *
     * Validates:
     * 1. Current password is correct (using BCrypt matches).
     * 2. New password meets length requirement (min 6 characters).
     * 3. New password matches confirmation password.
     */
    public User changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        User user = getUserByEmail(email);

        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new IllegalArgumentException("Current password is required.");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        // Verify current password against stored BCrypt hash
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // Hash new password using BCrypt and save
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Delete a user by their ID.
     * First checks if the user exists, then deletes them.
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);  // Check if user exists
        userRepository.delete(user);
    }
}
