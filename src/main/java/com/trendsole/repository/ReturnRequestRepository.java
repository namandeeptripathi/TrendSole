package com.trendsole.repository;

import com.trendsole.model.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    Optional<ReturnRequest> findByReturnNumber(String returnNumber);

    List<ReturnRequest> findByCustomerIdOrderByRequestedAtDesc(Long customerId);

    List<ReturnRequest> findByOrderEmailOrderByRequestedAtDesc(String email);

    boolean existsByOrderIdAndOrderItemId(Long orderId, Long orderItemId);

    boolean existsByOrderIdAndOrderItemIdIsNull(Long orderId);
}
