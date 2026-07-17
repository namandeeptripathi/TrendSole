package com.trendsole.service;

import com.trendsole.dto.OrderRequest;
import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.CartItem;
import com.trendsole.model.Order;
import com.trendsole.model.OrderItem;
import com.trendsole.model.OrderStatus;
import com.trendsole.model.Product;
import com.trendsole.model.User;
import com.trendsole.repository.OrderRepository;
import com.trendsole.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderStatusHistoryService statusHistoryService;

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
            throw new AccessDeniedException("You are not authorized to view this order.");
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
        order.setStatus(OrderStatus.PENDING);
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

        // Record initial timeline entry
        statusHistoryService.recordStatusChange(savedOrder, OrderStatus.PENDING, "SYSTEM", "Order placed");

        // Clear active cart after successful order creation
        cartService.clearCart();

        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        return updateOrderStatus(orderId, status, "ADMIN", null);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status, String updatedBy, String remarks) {
        Order order = getOrderById(orderId);
        OrderStatus newStatus = normalizeStatus(status);
        if (order.getStatus() != newStatus) {
            order.setStatus(newStatus);
            Order savedOrder = orderRepository.save(order);
            String historyRemarks = remarks != null ? remarks : "Order status updated to " + newStatus.name();
            statusHistoryService.recordStatusChange(savedOrder, newStatus, updatedBy != null ? updatedBy : "ADMIN", historyRemarks);
            return savedOrder;
        }
        return order;
    }

    private OrderStatus normalizeStatus(String status) {
        if (status == null || status.isBlank()) return OrderStatus.PENDING;
        String s = status.trim().toUpperCase();
        try {
            return OrderStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            String lower = status.trim().toLowerCase();
            if (lower.contains("confirm")) return OrderStatus.CONFIRMED;
            if (lower.contains("ship")) return OrderStatus.SHIPPED;
            if (lower.contains("deliver")) return OrderStatus.DELIVERED;
            if (lower.contains("cancel")) return OrderStatus.CANCELLED;
            if (lower.contains("process")) return OrderStatus.PROCESSING;
            return OrderStatus.PENDING;
        }
    }


    @Transactional
    public Order cancelOrder(Long orderId, String email) {
        Order order = getOrderById(orderId);
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);

        boolean isOwner = false;
        if (user != null && order.getUser() != null && order.getUser().getId().equals(user.getId())) {
            isOwner = true;
        } else if (order.getEmail() != null && order.getEmail().equalsIgnoreCase(email)) {
            isOwner = true;
        }

        if (!isOwner) {
            throw new AccessDeniedException("You are not authorized to cancel this order.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }

        if (order.getStatus() != OrderStatus.PENDING &&
            order.getStatus() != OrderStatus.CONFIRMED &&
            order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order cannot be cancelled after shipment.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    int currentStock = product.getStock() != null ? product.getStock() : 0;
                    product.setStock(currentStock + item.getQuantity());
                }
            }
        }

        Order savedOrder = orderRepository.save(order);
        statusHistoryService.recordStatusChange(savedOrder, OrderStatus.CANCELLED, "CUSTOMER", "Order cancelled");
        return savedOrder;
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

