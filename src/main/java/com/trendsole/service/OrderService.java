package com.trendsole.service;

import com.trendsole.dto.OrderRequest;
import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.CartItem;
import com.trendsole.model.Order;
import com.trendsole.model.OrderItem;
import com.trendsole.model.User;
import com.trendsole.repository.OrderRepository;
import com.trendsole.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> orderRepository.findByUserIdOrderByOrderDateDesc(user.getId()))
                .orElseGet(() -> orderRepository.findByEmailOrderByOrderDateDesc(email));
    }

    public Order getOrderByIdForUser(Long orderId, String email) {
        Order order = getOrderById(orderId);
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);

        boolean isOwner = false;
        if (user != null && order.getUser() != null && order.getUser().getId().equals(user.getId())) {
            isOwner = true;
        } else if (order.getEmail() != null && order.getEmail().equalsIgnoreCase(email)) {
            isOwner = true;
        } else if (user != null && "ADMIN".equalsIgnoreCase(String.valueOf(user.getRole()))) {
            isOwner = true;
        }

        if (!isOwner) {
            throw new SecurityException("You are not authorized to view this order.");
        }
        return order;
    }

    public Order placeOrder(OrderRequest request, String authenticatedEmail) {
        List<CartItem> cartItems = cartService.getAllCartItems();
        if (cartItems.isEmpty()) {

            throw new IllegalStateException("Cannot place order with an empty cart.");
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(request.getCustomerName());
        order.setEmail(request.getEmail());
        order.setAddress(request.getAddress());
        order.setPaymentMethod(formatPaymentMethod(request.getPaymentMethod()));
        order.setStatus("Pending");
        order.setOrderDate(LocalDateTime.now());

        // Associate authenticated user if available, or find by provided email
        String emailToUse = (authenticatedEmail != null && !authenticatedEmail.isBlank()) ? authenticatedEmail : request.getEmail();
        userRepository.findByEmailIgnoreCase(emailToUse).ifPresent(order::setUser);

        // Convert cart items to OrderItems
        double subtotal = 0.0;
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            subtotal += cartItem.getProduct().getPrice() * cartItem.getQuantity();

            order.getOrderItems().add(orderItem);
        }

        double totalWithTax = subtotal + (subtotal * 0.18); // Including 18% GST
        order.setTotalAmount(Math.round(totalWithTax * 100.0) / 100.0);

        Order savedOrder = orderRepository.save(order);

        // Clear active cart after successful order creation
        cartService.clearCart();

        return savedOrder;
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    private String generateOrderNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + dateStr + "-" + randomSuffix;
    }

    private String formatPaymentMethod(String input) {
        if (input == null || input.isBlank()) return "Cash on Delivery";
        switch (input.toLowerCase()) {
            case "cod":
                return "Cash on Delivery (COD)";
            case "upi":
                return "UPI Payment";
            case "card":
                return "Credit / Debit Card";
            default:
                return input;
        }
    }
}

