package com.trendsole.controller;

import com.trendsole.model.CartItem;
import com.trendsole.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CartController - REST API Controller for Shopping Cart
 *
 * What it does:
 * - Handles all cart-related HTTP requests from the frontend.
 * - URLs start with: http://localhost:8080/api/cart
 *
 * API Endpoints:
 * - GET    /api/cart           → View all cart items
 * - POST   /api/cart/add       → Add product to cart
 * - PUT    /api/cart/{id}      → Update item quantity
 * - DELETE /api/cart/{id}      → Remove item from cart
 * - DELETE /api/cart/clear     → Clear entire cart
 * - GET    /api/cart/total     → Get cart total price
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * GET /api/cart → Get all items in the cart.
     * Returns: List of cart items with product details.
     */
    @GetMapping
    public List<CartItem> getAllCartItems() {
        return cartService.getAllCartItems();
    }

    /**
     * POST /api/cart/add?productId=3&quantity=2 → Add a product to the cart.
     *
     * @RequestParam productId → Which product to add (e.g., 3).
     * @RequestParam quantity  → How many to add (default is 1).
     *
     * If the product is already in the cart, quantity is increased.
     */
    @PostMapping("/add")
    public CartItem addToCart(@RequestParam Long productId,
                             @RequestParam(defaultValue = "1") Integer quantity) {
        return cartService.addToCart(productId, quantity);
    }

    /**
     * PUT /api/cart/{id}?quantity=5 → Update the quantity of a cart item.
     * Example: PUT /api/cart/2?quantity=5 → Set cart item 2's quantity to 5.
     */
    @PutMapping("/{id}")
    public CartItem updateCartItem(@PathVariable Long id, @RequestParam Integer quantity) {
        return cartService.updateCartItem(id, quantity);
    }

    /**
     * DELETE /api/cart/{id} → Remove a single item from the cart.
     * Example: DELETE /api/cart/2 → Removes cart item with id=2.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.ok("Item removed from cart!");
    }

    /**
     * DELETE /api/cart/clear → Remove ALL items from the cart.
     * Used after placing an order or when user wants to start fresh.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("Cart cleared successfully!");
    }

    /**
     * GET /api/cart/total → Get the total price of all items in the cart.
     * Returns: JSON object like { "total": 25998.00 }
     *
     * We use a Map to return a JSON object instead of just a number.
     */
    @GetMapping("/total")
    public Map<String, Double> getCartTotal() {
        Map<String, Double> response = new HashMap<>();
        response.put("total", cartService.getCartTotal());
        return response;
    }
}
