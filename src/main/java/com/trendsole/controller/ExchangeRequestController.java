package com.trendsole.controller;

import com.trendsole.dto.ExchangeAdminActionDTO;
import com.trendsole.dto.ExchangeRequestCreateDTO;
import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.ExchangeRequest;
import com.trendsole.service.ExchangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
public class ExchangeRequestController {

    @Autowired
    private ExchangeRequestService exchangeRequestService;

    /**
     * POST /api/exchanges → Customer submits an exchange request with optional image files (Multipart).
     */
    @PostMapping(value = "/api/exchanges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createExchangeMultipart(
            @RequestParam("orderId") Long orderId,
            @RequestParam("orderItemId") Long orderItemId,
            @RequestParam(value = "exchangeReason", required = false) String exchangeReason,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "requestedSize", required = false) String requestedSize,
            @RequestParam(value = "requestedColor", required = false) String requestedColor,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            ExchangeRequestCreateDTO dto = new ExchangeRequestCreateDTO(orderId, orderItemId, exchangeReason, description, requestedSize, requestedColor);
            String email = auth.getName();
            ExchangeRequest created = exchangeRequestService.createExchangeRequest(dto, files, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * POST /api/exchanges → Customer submits an exchange request via JSON (No files).
     */
    @PostMapping(value = "/api/exchanges", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createExchangeJson(@RequestBody ExchangeRequestCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            String email = auth.getName();
            ExchangeRequest created = exchangeRequestService.createExchangeRequest(dto, null, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * GET /api/exchanges/my → Get exchange requests for currently logged-in customer.
     */
    @GetMapping("/api/exchanges/my")
    public ResponseEntity<?> getMyExchanges() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        String email = auth.getName();
        List<ExchangeRequest> exchanges = exchangeRequestService.getMyExchangeRequests(email);
        return ResponseEntity.ok(exchanges);
    }

    /**
     * GET /api/exchanges/{id} → Get exchange request details by ID.
     */
    @GetMapping("/api/exchanges/{id}")
    public ResponseEntity<?> getExchangeById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            String email = auth.getName();
            ExchangeRequest req = exchangeRequestService.getExchangeByIdForCustomer(id, email);
            return ResponseEntity.ok(req);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/exchanges → Get all exchange requests for Admin.
     */
    @GetMapping("/api/admin/exchanges")
    public ResponseEntity<?> getAllExchanges() {
        List<ExchangeRequest> list = exchangeRequestService.getAllExchangeRequests();
        return ResponseEntity.ok(list);
    }

    /**
     * PUT /api/admin/exchanges/{id}/approve → Admin approves an exchange request.
     */
    @PutMapping("/api/admin/exchanges/{id}/approve")
    public ResponseEntity<?> approveExchange(@PathVariable Long id, @RequestBody(required = false) ExchangeAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ExchangeRequest updated = exchangeRequestService.approveExchange(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/exchanges/{id}/reject → Admin rejects an exchange request.
     */
    @PutMapping("/api/admin/exchanges/{id}/reject")
    public ResponseEntity<?> rejectExchange(@PathVariable Long id, @RequestBody(required = false) ExchangeAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ExchangeRequest updated = exchangeRequestService.rejectExchange(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/exchanges/{id}/reserve → Admin reserves replacement product.
     */
    @PutMapping("/api/admin/exchanges/{id}/reserve")
    public ResponseEntity<?> reserveExchange(@PathVariable Long id, @RequestBody(required = false) ExchangeAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ExchangeRequest updated = exchangeRequestService.reserveProduct(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/exchanges/{id}/ship → Admin ships replacement product.
     */
    @PutMapping("/api/admin/exchanges/{id}/ship")
    public ResponseEntity<?> shipExchange(@PathVariable Long id, @RequestBody(required = false) ExchangeAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ExchangeRequest updated = exchangeRequestService.shipExchange(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/exchanges/{id}/deliver → Admin marks replacement product as delivered.
     */
    @PutMapping("/api/admin/exchanges/{id}/deliver")
    public ResponseEntity<?> deliverExchange(@PathVariable Long id, @RequestBody(required = false) ExchangeAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ExchangeRequest updated = exchangeRequestService.deliverExchange(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/exchanges/{id}/complete → Admin completes exchange request.
     */
    @PutMapping("/api/admin/exchanges/{id}/complete")
    public ResponseEntity<?> completeExchange(@PathVariable Long id, @RequestBody(required = false) ExchangeAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ExchangeRequest updated = exchangeRequestService.completeExchange(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
