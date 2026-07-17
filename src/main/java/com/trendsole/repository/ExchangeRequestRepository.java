package com.trendsole.repository;

import com.trendsole.model.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    Optional<ExchangeRequest> findByExchangeNumber(String exchangeNumber);

    List<ExchangeRequest> findByCustomerIdOrderByRequestedAtDesc(Long customerId);

    List<ExchangeRequest> findByOrderEmailOrderByRequestedAtDesc(String email);

    boolean existsByOrderIdAndOrderItemId(Long orderId, Long orderItemId);
}
