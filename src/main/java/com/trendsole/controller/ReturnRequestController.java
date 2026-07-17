package com.trendsole.controller;

import com.trendsole.dto.InspectionUpdateDTO;
import com.trendsole.dto.RefundUpdateDTO;
import com.trendsole.dto.ReturnAdminActionDTO;
import com.trendsole.dto.ReturnRequestCreateDTO;
import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.ReturnRequest;
import com.trendsole.service.ReturnRequestService;
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
public class ReturnRequestController {

    @Autowired
    private ReturnRequestService returnRequestService;

    /**
     * POST /api/returns → Customer submits a return request with optional image files.
     */
    @PostMapping(value = "/api/returns", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReturnMultipart(
            @RequestParam("orderId") Long orderId,
            @RequestParam(value = "orderItemId", required = false) Long orderItemId,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            ReturnRequestCreateDTO dto = new ReturnRequestCreateDTO(orderId, orderItemId, reason, description);
            String email = auth.getName();
            ReturnRequest created = returnRequestService.createReturnRequest(dto, files, email);
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
     * POST /api/returns → Customer submits a return request via JSON body (no files).
     */
    @PostMapping(value = "/api/returns", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReturnJson(@RequestBody ReturnRequestCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            String email = auth.getName();
            ReturnRequest created = returnRequestService.createReturnRequest(dto, null, email);
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
     * GET /api/returns/my → Get return requests for currently logged-in customer.
     */
    @GetMapping("/api/returns/my")
    public ResponseEntity<?> getMyReturns() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        String email = auth.getName();
        List<ReturnRequest> returns = returnRequestService.getMyReturnRequests(email);
        return ResponseEntity.ok(returns);
    }

    /**
     * GET /api/returns/{id} → Get return request details by ID for customer/admin.
     */
    @GetMapping("/api/returns/{id}")
    public ResponseEntity<?> getReturnById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "User not logged in"));
        }

        try {
            String email = auth.getName();
            ReturnRequest returnReq = returnRequestService.getReturnByIdForCustomer(id, email);
            return ResponseEntity.ok(returnReq);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/returns → Get all return requests for Admin.
     */
    @GetMapping("/api/admin/returns")
    public ResponseEntity<?> getAllReturns() {
        List<ReturnRequest> returns = returnRequestService.getAllReturnRequests();
        return ResponseEntity.ok(returns);
    }

    /**
     * PUT /api/admin/returns/{id}/approve → Admin approves a return request.
     */
    @PutMapping("/api/admin/returns/{id}/approve")
    public ResponseEntity<?> approveReturn(@PathVariable Long id, @RequestBody(required = false) ReturnAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ReturnRequest updated = returnRequestService.approveReturn(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/returns/{id}/reject → Admin rejects a return request.
     */
    @PutMapping("/api/admin/returns/{id}/reject")
    public ResponseEntity<?> rejectReturn(@PathVariable Long id, @RequestBody(required = false) ReturnAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ReturnRequest updated = returnRequestService.rejectReturn(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/returns/{id}/inspection → Admin updates inspection status.
     */
    @PutMapping("/api/admin/returns/{id}/inspection")
    public ResponseEntity<?> updateInspection(@PathVariable Long id, @RequestBody InspectionUpdateDTO dto) {
        try {
            ReturnRequest updated = returnRequestService.updateInspectionStatus(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/returns/{id}/refund → Admin updates refund status.
     */
    @PutMapping("/api/admin/returns/{id}/refund")
    public ResponseEntity<?> updateRefund(@PathVariable Long id, @RequestBody RefundUpdateDTO dto) {
        try {
            ReturnRequest updated = returnRequestService.updateRefundStatus(id, dto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/returns/{id}/complete → Admin completes a return request.
     */
    @PutMapping("/api/admin/returns/{id}/complete")
    public ResponseEntity<?> completeReturn(@PathVariable Long id, @RequestBody(required = false) ReturnAdminActionDTO dto) {
        try {
            String remarks = dto != null ? dto.getAdminRemarks() : null;
            ReturnRequest updated = returnRequestService.completeReturn(id, remarks);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
