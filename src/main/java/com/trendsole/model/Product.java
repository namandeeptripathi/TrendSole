package com.trendsole.model;

// JPA annotations - used to map this Java class to a database table
import jakarta.persistence.Entity;
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

/**
 * Product Entity Class
 *
 * What is an Entity?
 * - An Entity is a Java class that represents a TABLE in the database.
 * - Each object of this class = one ROW in the "products" table.
 * - Each field in this class = one COLUMN in the table.
 *
 * Example:
 * - Product object with name="Nike Air Max" becomes a row in the products table.
 *
 * Annotations explained:
 * - @Entity   → Tells Hibernate "this class is a database table"
 * - @Table    → Specifies the exact table name in MySQL
 * - @Getter   → Lombok auto-generates all getter methods (e.g., getName(), getPrice())
 * - @Setter   → Lombok auto-generates all setter methods (e.g., setName(), setPrice())
 * - @NoArgsConstructor  → Creates an empty constructor: Product()
 * - @AllArgsConstructor → Creates a constructor with all fields: Product(id, name, ...)
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

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
     * Product name (e.g., "Nike Air Max 90")
     * @Column(nullable = false) → This field cannot be empty in the database.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Product description (e.g., "Classic sneakers with Air cushioning")
     * @Column(columnDefinition = "TEXT") → Stores long text in the database.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Product price (e.g., 12999.00)
     * @Column(nullable = false) → Price is required, cannot be null.
     */
    @Column(nullable = false)
    private Double price;

    /**
     * Product category (e.g., "Sneakers", "Formal", "Boots")
     */
    @Column(length = 100)
    private String category;

    /**
     * URL or path of the product image
     * @Column(name = "image_url") → Maps this field to the "image_url" column in DB.
     * Note: Java uses camelCase (imageUrl) but DB uses snake_case (image_url).
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Number of items available in stock (e.g., 25)
     * @Column(nullable = false) → Stock is required.
     * Default value is handled in the database (see schema.sql).
     */
    @Column(nullable = false)
    private Integer stock;
}
