package com.trendsole.repository;

import com.trendsole.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CartItemRepository - Database Access Layer for Cart Items
 *
 * What it does:
 * - Handles all database operations for the "cart" table.
 * - Extends JpaRepository to get built-in CRUD methods.
 *
 * JpaRepository<CartItem, Long>:
 * - CartItem → The entity this repository manages.
 * - Long     → Data type of the primary key.
 *
 * Built-in methods available:
 * - findAll()          → Get all items in the cart
 * - findById(id)       → Find a specific cart item
 * - save(cartItem)     → Add or update a cart item
 * - deleteById(id)     → Remove a cart item
 * - deleteAll()        → Empty the entire cart
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find a cart item by its product ID.
     * This helps check if a product is already in the cart before adding it again.
     * findByProductId(3) → SELECT * FROM cart WHERE product_id = 3
     */
    Optional<CartItem> findByProductId(Long productId);
}
