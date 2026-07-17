package com.trendsole.dto;

import com.trendsole.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryDTO {
    private OrderStatus status;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private String remarks;
}
