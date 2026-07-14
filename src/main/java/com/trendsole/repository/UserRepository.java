package com.trendsole.repository;

import com.trendsole.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Database Access Layer for Users
 *
 * What is this?
 * - This Repository handles all database operations for the User entity.
 * - We don't write SQL queries manually — Spring Data JPA does it for us!
 *
 * JpaRepository<User, Long>:
 * - User → The entity class this repository works with.
 * - Long → The data type of the primary key (id).
 *
 * Built-in methods we get for FREE (no need to write them):
 * - findAll()      → SELECT * FROM users
 * - findById(id)   → SELECT * FROM users WHERE id = ?
 * - save(user)     → INSERT or UPDATE a user
 * - deleteById(id) → DELETE FROM users WHERE id = ?
 * - count()        → SELECT COUNT(*) FROM users
 *
 * @Repository → Tells Spring that this is a database access component.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Custom query method: Find a user by their email address.
     * Spring JPA automatically creates the SQL query from the method name!
     * findByEmail("naman@example.com") → SELECT * FROM users WHERE email = 'naman@example.com'
     *
     * Returns Optional<User> because the user might not exist.
     */
    Optional<User> findByEmail(String email);

    /**
     * Custom query method: Check if a user with this email already exists.
     * existsByEmail("naman@example.com") → SELECT COUNT(*) > 0 FROM users WHERE email = ?
     *
     * Returns true if the email is already taken, false otherwise.
     * Useful during registration to prevent duplicate accounts.
     */
    boolean existsByEmail(String email);
}
