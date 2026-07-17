package com.trendsole.service;

import com.trendsole.dto.OrderStatusHistoryDTO;
import com.trendsole.model.Order;
import com.trendsole.model.OrderStatus;
import com.trendsole.model.OrderStatusHistory;
import com.trendsole.repository.OrderStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderStatusHistoryService {

    @Autowired
    private OrderStatusHistoryRepository historyRepository;

    @Transactional
    public OrderStatusHistory recordStatusChange(Order order, OrderStatus status, String updatedBy, String remarks) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setUpdatedAt(LocalDateTime.now());
        history.setUpdatedBy(updatedBy != null ? updatedBy : "SYSTEM");
        history.setRemarks(remarks);
        return historyRepository.save(history);
    }

    public List<OrderStatusHistoryDTO> getTimelineByOrderId(Long orderId) {
        return historyRepository.findByOrderIdOrderByUpdatedAtAsc(orderId)
                .stream()
                .map(h -> new OrderStatusHistoryDTO(
                        h.getStatus(),
                        h.getUpdatedAt(),
                        h.getUpdatedBy(),
                        h.getRemarks()))
                .collect(Collectors.toList());
    }
}
