package com.trendsole.controller;

import com.trendsole.model.Product;
import com.trendsole.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController - REST API Controller for Products
 *
 * What is a Controller?
 * - The Controller is the ENTRY POINT of our application.
 * - It receives HTTP requests from the frontend (browser/JavaScript).
 * - It calls the Service layer to process the request.
 * - It sends back a response (usually JSON data).
 *
 * @RestController → Combines @Controller + @ResponseBody.
 *   - Tells Spring this class handles REST API requests.
 *   - All methods return JSON data (not HTML pages).
 *
 * @RequestMapping("/api/products") → Base URL for all product APIs.
 *   - All URLs in this controller start with: http://localhost:8080/api/products
 *
 * @CrossOrigin → Allows requests from the frontend (different port/origin).
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * GET /api/products → Get all products.
     * HTTP Method: GET
     * Example: http://localhost:8080/api/products
     * Returns: List of all products in JSON format.
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * GET /api/products/{id} → Get a single product by ID.
     * HTTP Method: GET
     * Example: http://localhost:8080/api/products/3
     * @PathVariable → Extracts the {id} from the URL.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);  // Returns 200 OK with product data
    }

    /**
     * GET /api/products/category/{category} → Get products by category.
     * HTTP Method: GET
     * Example: http://localhost:8080/api/products/category/Sneakers
     */
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    /**
     * GET /api/products/search?name=nike → Search products by name.
     * HTTP Method: GET
     * Example: http://localhost:8080/api/products/search?name=nike
     * @RequestParam → Extracts the "name" query parameter from the URL.
     */
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }

    /**
     * POST /api/products → Add a new product.
     * HTTP Method: POST
     * @RequestBody → The product data is sent in the request body as JSON.
     * Returns: The saved product with its generated ID.
     */
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    /**
     * PUT /api/products/{id} → Update an existing product.
     * HTTP Method: PUT
     * Example: http://localhost:8080/api/products/3
     * @RequestBody → Updated product data in JSON.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE /api/products/{id} → Delete a product.
     * HTTP Method: DELETE
     * Example: http://localhost:8080/api/products/3
     * Returns: A success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }
}
