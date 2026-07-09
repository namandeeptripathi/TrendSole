package com.trendsole.service;

import com.trendsole.model.Order;
import com.trendsole.repository.OrderRepository;
import com.trendsole.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderService - Business Logic Layer for Orders
 *
 * What it does:
 * - Handles all order-related business logic.
 * - Creates new orders, fetches order history, etc.
 *
 * Flow:
 * - User clicks "Place Order" → Controller → OrderService → OrderRepository → Database
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    /**
     * Get all orders from the database.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Get a single order by its ID.
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    /**
     * Get all orders placed by a specific customer (using their email).
     * Example: getOrdersByEmail("john@gmail.com") → returns all of John's orders.
     */
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByEmail(email);
    }

    /**
     * Place a new order.
     *
     * Steps:
     * 1. Set the order date to the current date and time.
     * 2. Save the order to the database.
     * 3. Clear the cart (because the order has been placed).
     *
     * @param order → Order object with customer details and total amount.
     */
    public Order placeOrder(Order order) {
        // Step 1: Set the current date and time as the order date
        order.setOrderDate(LocalDateTime.now());

        // Step 2: Save the order to the database
        Order savedOrder = orderRepository.save(order);

        // Step 3: Clear the cart after placing the order
        cartService.clearCart();

        return savedOrder;
    }

    /**
     * Delete an order by its ID.
     */
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);  // Check if it exists first
        orderRepository.delete(order);
    }
}
