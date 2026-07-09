package com.trendsole.repository;

import com.trendsole.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderRepository - Database Access Layer for Orders
 *
 * What it does:
 * - Handles all database operations for the "orders" table.
 * - Extends JpaRepository to get built-in CRUD methods.
 *
 * JpaRepository<Order, Long>:
 * - Order → The entity this repository manages.
 * - Long  → Data type of the primary key.
 *
 * Built-in methods available:
 * - findAll()        → Get all orders
 * - findById(id)     → Find a specific order
 * - save(order)      → Create a new order
 * - deleteById(id)   → Delete an order
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders by a customer's email.
     * This allows a user to see their order history.
     * findByEmail("john@gmail.com") → SELECT * FROM orders WHERE email = 'john@gmail.com'
     */
    List<Order> findByEmail(String email);
}
