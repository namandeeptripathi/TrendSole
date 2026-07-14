package com.trendsole.service;

import com.trendsole.exception.ResourceNotFoundException;
import com.trendsole.model.Address;
import com.trendsole.model.AddressType;
import com.trendsole.model.User;
import com.trendsole.repository.AddressRepository;
import com.trendsole.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AddressService — Business logic and validations for Address Management.
 */
@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Helper method to look up user entity by email case-insensitively.
     */
    private User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        return userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User account not found for email: " + email));
    }

    /**
     * Get all addresses belonging to the authenticated user.
     */
    public List<Address> getUserAddresses(String email) {
        User user = getUserByEmail(email);
        return addressRepository.findByUserIdOrderByIdDesc(user.getId());
    }

    /**
     * Get a specific address by ID, ensuring it belongs to the authenticated user.
     */
    public Address getAddressById(Long id, String email) {
        User user = getUserByEmail(email);
        return addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }

    /**
     * Create a new address for the authenticated user.
     * Enforces the single default address rule.
     */
    @Transactional
    public Address createAddress(String email, Address address) {
        User user = getUserByEmail(email);

        validateAddressFields(address);

        address.setUser(user);
        if (address.getAddressType() == null) {
            address.setAddressType(AddressType.HOME);
        }

        List<Address> existingAddresses = addressRepository.findByUserIdOrderByIdDesc(user.getId());

        // If this is the user's first address, automatically make it default
        if (existingAddresses.isEmpty()) {
            address.setIsDefault(true);
        } else if (Boolean.TRUE.equals(address.getIsDefault())) {
            // Unset previous default address(es)
            clearDefaultAddressFlags(user.getId());
        } else {
            address.setIsDefault(false);
        }

        return addressRepository.save(address);
    }

    /**
     * Update an existing address belonging to the authenticated user.
     */
    @Transactional
    public Address updateAddress(Long id, String email, Address updatedDetails) {
        User user = getUserByEmail(email);
        Address existingAddress = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        validateAddressFields(updatedDetails);

        existingAddress.setFullName(updatedDetails.getFullName().trim());
        existingAddress.setPhoneNumber(updatedDetails.getPhoneNumber().trim());
        existingAddress.setAddressLine1(updatedDetails.getAddressLine1().trim());
        existingAddress.setAddressLine2(updatedDetails.getAddressLine2() != null ? updatedDetails.getAddressLine2().trim() : null);
        existingAddress.setLandmark(updatedDetails.getLandmark() != null ? updatedDetails.getLandmark().trim() : null);
        existingAddress.setCity(updatedDetails.getCity().trim());
        existingAddress.setState(updatedDetails.getState().trim());
        existingAddress.setPincode(updatedDetails.getPincode().trim());
        existingAddress.setCountry(updatedDetails.getCountry().trim());

        if (updatedDetails.getAddressType() != null) {
            existingAddress.setAddressType(updatedDetails.getAddressType());
        }

        if (Boolean.TRUE.equals(updatedDetails.getIsDefault())) {
            clearDefaultAddressFlags(user.getId());
            existingAddress.setIsDefault(true);
        }

        return addressRepository.save(existingAddress);
    }

    /**
     * Set a specific address as default for the authenticated user.
     * Clears default flag from all other addresses belonging to the user.
     */
    @Transactional
    public Address setDefaultAddress(Long id, String email) {
        User user = getUserByEmail(email);
        Address targetAddress = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        clearDefaultAddressFlags(user.getId());
        targetAddress.setIsDefault(true);

        return addressRepository.save(targetAddress);
    }

    /**
     * Delete an address belonging to the authenticated user.
     * If the deleted address was default, reassign default status to the newest remaining address.
     */
    @Transactional
    public void deleteAddress(Long id, String email) {
        User user = getUserByEmail(email);
        Address addressToDelete = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        boolean wasDefault = Boolean.TRUE.equals(addressToDelete.getIsDefault());

        addressRepository.delete(addressToDelete);

        // If the deleted address was default, promote the newest remaining address to default
        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserIdOrderByIdDesc(user.getId());
            if (!remaining.isEmpty()) {
                Address newest = remaining.get(0);
                newest.setIsDefault(true);
                addressRepository.save(newest);
            }
        }
    }

    /**
     * Helper method to clear isDefault flag for all user addresses.
     */
    private void clearDefaultAddressFlags(Long userId) {
        List<Address> defaults = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        for (Address addr : defaults) {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        }
    }

    /**
     * Validate required address fields, phone number format, and pincode.
     */
    private void validateAddressFields(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address request body cannot be empty.");
        }
        if (isEmpty(address.getFullName())) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (isEmpty(address.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone Number is required.");
        }
        if (address.getPhoneNumber().trim().length() < 7) {
            throw new IllegalArgumentException("Please enter a valid Phone Number.");
        }
        if (isEmpty(address.getAddressLine1())) {
            throw new IllegalArgumentException("Address Line 1 is required.");
        }
        if (isEmpty(address.getCity())) {
            throw new IllegalArgumentException("City is required.");
        }
        if (isEmpty(address.getState())) {
            throw new IllegalArgumentException("State is required.");
        }
        if (isEmpty(address.getPincode())) {
            throw new IllegalArgumentException("Pincode is required.");
        }
        if (address.getPincode().trim().length() < 3 || address.getPincode().trim().length() > 10) {
            throw new IllegalArgumentException("Please enter a valid Pincode.");
        }
        if (isEmpty(address.getCountry())) {
            throw new IllegalArgumentException("Country is required.");
        }
    }

    private boolean isEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }
}
