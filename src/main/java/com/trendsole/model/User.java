package com.trendsole.model;

// JPA annotations - used to map this Java class to a database table
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

// Lombok annotations - auto-generates getters, setters, and constructors
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * User Entity Class
 *
 * What is this?
 * - This Entity represents the "users" table in the database.
 * - Each object of this class = one ROW in the "users" table.
 * - Each field in this class = one COLUMN in the table.
 *
 * Example:
 * - A User object with fullName="Naman Tripathi", email="naman@example.com"
 *   becomes a row in the users table.
 *
 * Annotations explained:
 * - @Entity   → Tells Hibernate "this class is a database table"
 * - @Table    → Specifies the exact table name in MySQL
 * - @Getter   → Lombok auto-generates all getter methods
 * - @Setter   → Lombok auto-generates all setter methods
 * - @NoArgsConstructor  → Creates an empty constructor: User()
 * - @AllArgsConstructor → Creates a constructor with all fields
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * @Id → Marks this field as the PRIMARY KEY of the table.
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     *   → The database will auto-generate this ID (1, 2, 3, ...).
     *   → We don't need to set it manually.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the user (e.g., "Naman Tripathi")
     * @Column(nullable = false) → This field cannot be empty in the database.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * Email address of the user (e.g., "naman@example.com")
     * @Column(nullable = false, unique = true) → Email is required and must be unique.
     * No two users can have the same email address.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Password of the user (stored as a BCrypt hash, NOT plain text).
     * @Column(nullable = false) → Password is required.
     * Note: The password is hashed before being saved to the database.
     * Example: "myPassword123" → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    /**
     * Phone number of the user (e.g., "+91 98765 43210")
     * This field is optional — the user can choose not to provide it.
     */
    @Column(length = 20)
    private String phoneNumber;

    /**
     * Role of the user in the system (USER or ADMIN).
     * @Enumerated(EnumType.STRING) → Stores the role as a String in the database
     *   (e.g., "USER" instead of 0).
     * Default role is USER — set automatically in the service layer during registration.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Timestamp of when the user account was created.
     * @Column(updatable = false) → This value is set once and never changed.
     * Set automatically in the service layer during registration.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
