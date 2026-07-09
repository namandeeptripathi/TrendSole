package com.trendsole.repository;

import com.trendsole.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductRepository - Database Access Layer for Products
 *
 * What is a Repository?
 * - A Repository is an INTERFACE that handles all database operations.
 * - We don't write SQL queries manually — Spring Data JPA does it for us!
 *
 * JpaRepository<Product, Long>:
 * - Product → The entity class this repository works with.
 * - Long    → The data type of the primary key (id).
 *
 * Built-in methods we get for FREE (no need to write them):
 * - findAll()        → SELECT * FROM products
 * - findById(id)     → SELECT * FROM products WHERE id = ?
 * - save(product)    → INSERT or UPDATE a product
 * - deleteById(id)   → DELETE FROM products WHERE id = ?
 * - count()          → SELECT COUNT(*) FROM products
 *
 * @Repository → Tells Spring that this is a database access component.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Custom query method: Find products by category.
     * Spring JPA automatically creates the SQL query from the method name!
     * findByCategory("Sneakers") → SELECT * FROM products WHERE category = 'Sneakers'
     */
    List<Product> findByCategory(String category);

    /**
     * Custom query method: Search products by name (case-insensitive, partial match).
     * findByNameContainingIgnoreCase("nike") → SELECT * FROM products WHERE LOWER(name) LIKE '%nike%'
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
