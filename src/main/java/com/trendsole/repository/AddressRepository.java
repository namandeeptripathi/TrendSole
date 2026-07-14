package com.trendsole.repository;

import com.trendsole.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AddressRepository - Spring Data JPA repository for Address operations.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Find all addresses belonging to a specific user ID ordered by newest first.
     */
    List<Address> findByUserIdOrderByIdDesc(Long userId);

    /**
     * Find a specific address by ID belonging to a specific user ID.
     */
    Optional<Address> findByIdAndUserId(Long id, Long userId);

    /**
     * Find current default address(es) for a user ID.
     */
    List<Address> findByUserIdAndIsDefaultTrue(Long userId);
}
