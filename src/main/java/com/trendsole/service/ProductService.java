package com.trendsole.service;

import com.trendsole.model.Product;
import com.trendsole.repository.ProductRepository;
import com.trendsole.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ProductService - Business Logic Layer for Products
 *
 * What is a Service class?
 * - The Service layer sits BETWEEN the Controller and the Repository.
 * - Controller → receives the request from the user.
 * - Service    → processes the business logic.
 * - Repository → talks to the database.
 *
 * Why do we need it?
 * - Keeps the Controller clean (Controller should only handle requests).
 * - All the "thinking" / logic happens here.
 *
 * @Service → Tells Spring "this class contains business logic".
 * @Autowired → Spring automatically creates and injects the ProductRepository object.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get ALL products from the database.
     * Calls: SELECT * FROM products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get a SINGLE product by its ID.
     * If the product is not found, throw an error with a helpful message.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Get products filtered by category.
     * Example: getProductsByCategory("Sneakers") returns only sneakers.
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Search products by name (partial, case-insensitive).
     * Example: searchProducts("nike") returns "Nike Air Max", "Nike Dunk", etc.
     */
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Add a new product to the database.
     * save() does INSERT if the product is new, or UPDATE if it already exists.
     */
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Update an existing product.
     * Steps: 1. Find the product by ID  2. Update its fields  3. Save it back.
     */
    public Product updateProduct(Long id, Product productDetails) {
        // Step 1: Find the existing product (throws error if not found)
        Product product = getProductById(id);

        // Step 2: Update the fields with new values
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());
        product.setStock(productDetails.getStock());

        // Step 3: Save updated product back to database
        return productRepository.save(product);
    }

    /**
     * Delete a product by its ID.
     * First checks if the product exists, then deletes it.
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);  // Check if it exists
        productRepository.delete(product);
    }
}
