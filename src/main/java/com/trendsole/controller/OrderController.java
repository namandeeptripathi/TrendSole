package com.trendsole.controller;

import com.trendsole.dto.OrderRequest;
import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.Order;
import com.trendsole.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private com.trendsole.service.OrderStatusHistoryService statusHistoryService;

    @Autowired
    private com.trendsole.service.InvoiceService invoiceService;

    /**
     * GET /api/orders → Get all orders (for Admin / general retrieval).
     */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * GET /api/orders/my-orders → Get order history for the currently logged in user.
     */
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        String email = auth.getName();
        List<Order> orders = orderService.getOrdersByEmail(email);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/{id} → Get order details by ID (Security checked: user can only view their own order).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            String email = auth.getName();
            Order order = orderService.getOrderByIdForUser(id, email);
            return ResponseEntity.ok(order);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/email/{email} → Get orders by customer email.
     */
    @GetMapping("/email/{email}")
    public List<Order> getOrdersByEmail(@PathVariable String email) {
        return orderService.getOrdersByEmail(email);
    }

    /**
     * POST /api/orders → Create/Place a new order.
     */
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) ? auth.getName() : null;

        Order createdOrder = orderService.placeOrder(orderRequest, authenticatedEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * PUT /api/orders/{id}/status → Update order status (Admin function).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * POST /api/orders/{id}/cancel → Cancel an order (Customer function).
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            String email = auth.getName();
            Order cancelledOrder = orderService.cancelOrder(id, email);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Order cancelled successfully.");
            response.put("orderId", cancelledOrder.getId());
            response.put("status", cancelledOrder.getStatus().name());
            response.put("cancelledAt", cancelledOrder.getCancelledAt());

            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/{id}/timeline → Get status timeline history for an order.
     */
    @GetMapping("/{id}/timeline")
    public ResponseEntity<?> getOrderTimeline(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            String email = auth.getName();
            // Validate authorization (Customer can view only their own order timeline, Admin can view any timeline)
            orderService.getOrderByIdForUser(id, email);

            List<com.trendsole.dto.OrderStatusHistoryDTO> timeline = statusHistoryService.getTimelineByOrderId(id);
            return ResponseEntity.ok(timeline);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/{id}/invoice → Download PDF Invoice for an order.
     */
    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String email = auth.getName();
            // Validate authorization (Customer can download only their own invoice, Admin can download any)
            Order order = orderService.getOrderByIdForUser(id, email);

            byte[] pdfBytes = invoiceService.generateInvoicePdf(order);

            String filename = "invoice-" + (order.getOrderNumber() != null ? order.getOrderNumber() : order.getId()) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(pdfBytes);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * DELETE /api/orders/{id} → Delete an order.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully!");
    }
}

