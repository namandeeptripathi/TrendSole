package com.trendsole.controller;

import com.trendsole.model.Address;
import com.trendsole.service.AddressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AddressController — REST Controller for Address Management.
 * Base URL: /api/addresses
 * All operations are restricted to the currently authenticated user session.
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * GET /api/addresses → Get all addresses for the logged-in user.
     */
    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses() {
        String email = getAuthenticatedUserEmail();
        List<Address> addresses = addressService.getUserAddresses(email);
        return ResponseEntity.ok(addresses);
    }

    /**
     * GET /api/addresses/{id} → Get single address by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        String email = getAuthenticatedUserEmail();
        Address address = addressService.getAddressById(id, email);
        return ResponseEntity.ok(address);
    }

    /**
     * POST /api/addresses → Add a new address.
     */
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        String email = getAuthenticatedUserEmail();
        Address createdAddress = addressService.createAddress(email, address);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    /**
     * PUT /api/addresses/{id} → Update an address.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        String email = getAuthenticatedUserEmail();
        Address updatedAddress = addressService.updateAddress(id, email, address);
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * PUT /api/addresses/{id}/default → Set address as default.
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<Address> setDefaultAddress(@PathVariable Long id) {
        String email = getAuthenticatedUserEmail();
        Address defaultAddress = addressService.setDefaultAddress(id, email);
        return ResponseEntity.ok(defaultAddress);
    }

    /**
     * DELETE /api/addresses/{id} → Delete an address.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAddress(@PathVariable Long id) {
        String email = getAuthenticatedUserEmail();
        addressService.deleteAddress(id, email);
        return ResponseEntity.ok(Map.of("message", "Address deleted successfully!"));
    }

    /**
     * Helper method to extract current user's email from SecurityContext.
     */
    private String getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalArgumentException("User is not authenticated. Please log in.");
        }
        return auth.getName();
    }

    /**
     * Exception handler for IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Exception handler for general exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "An error occurred: " + ex.getClass().getSimpleName();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
}
