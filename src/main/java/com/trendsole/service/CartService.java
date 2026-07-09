package com.trendsole.service;

import com.trendsole.model.CartItem;
import com.trendsole.model.Product;
import com.trendsole.repository.CartItemRepository;
import com.trendsole.repository.ProductRepository;
import com.trendsole.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * CartService - Business Logic Layer for Shopping Cart
 *
 * What it does:
 * - Manages all cart operations: add items, remove items, update quantity, clear cart.
 * - Contains the logic for handling duplicate products in the cart.
 *
 * Flow:
 * - User clicks "Add to Cart" → Controller → CartService → CartItemRepository → Database
 */
@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get all items currently in the cart.
     */
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    /**
     * Add a product to the cart.
     *
     * Logic:
     * - If the product is ALREADY in the cart → increase the quantity.
     * - If the product is NOT in the cart → add it as a new item.
     *
     * @param productId → The ID of the product to add.
     * @param quantity  → How many to add.
     */
    public CartItem addToCart(Long productId, Integer quantity) {
        // Step 1: Find the product in the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Step 2: Check if this product is already in the cart
        Optional<CartItem> existingItem = cartItemRepository.findByProductId(productId);

        if (existingItem.isPresent()) {
            // Product already in cart → just increase the quantity
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            // Product not in cart → create a new cart item
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * Update the quantity of a cart item.
     * Example: User changes quantity from 1 to 3.
     */
    public CartItem updateCartItem(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Remove a single item from the cart.
     */
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cartItemRepository.delete(cartItem);
    }

    /**
     * Clear the entire cart (remove all items).
     * Used after placing an order.
     */
    public void clearCart() {
        cartItemRepository.deleteAll();
    }

    /**
     * Calculate the total price of all items in the cart.
     * Formula: For each item → product price × quantity, then sum them all.
     */
    public Double getCartTotal() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        Double total = 0.0;

        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        return total;
    }
}
