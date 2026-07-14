package com.trendsole.controller;

import com.trendsole.model.Order;
import com.trendsole.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OrderController - REST API Controller for Orders
 *
 * What it does:
 * - Handles all order-related HTTP requests from the frontend.
 * - URLs start with: http://localhost:8080/api/orders
 *
 * API Endpoints:
 * - GET    /api/orders               → Get all orders
 * - GET    /api/orders/{id}          → Get order by ID
 * - GET    /api/orders/email/{email} → Get orders by customer email
 * - POST   /api/orders               → Place a new order
 * - DELETE /api/orders/{id}          → Delete an order
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * GET /api/orders → Get all orders.
     * Returns: List of all orders in JSON format.
     */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * GET /api/orders/{id} → Get a single order by ID.
     * Example: http://localhost:8080/api/orders/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * GET /api/orders/email/{email} → Get all orders by a customer's email.
     * Example: http://localhost:8080/api/orders/email/john@gmail.com
     * This is used to show a customer their order history.
     */
    @GetMapping("/email/{email}")
    public List<Order> getOrdersByEmail(@PathVariable String email) {
        return orderService.getOrdersByEmail(email);
    }

    /**
     * POST /api/orders → Place a new order.
     *
     * @RequestBody → The order data (customerName, email, address, totalAmount)
     *                 is sent as JSON in the request body.
     *
     * Example JSON body:
     * {
     *   "customerName": "Namandeep",
     *   "email": "naman@gmail.com",
     *   "address": "123 Main Street, Delhi",
     *   "totalAmount": 25998.00
     * }
     *
     * Note: orderDate is set automatically by the service layer.
     */
    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        return orderService.placeOrder(order);
    }

    /**
     * DELETE /api/orders/{id} → Delete an order by ID.
     * Example: DELETE http://localhost:8080/api/orders/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully!");
    }
}
