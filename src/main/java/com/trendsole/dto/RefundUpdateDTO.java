package com.trendsole.dto;

import com.trendsole.model.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefundUpdateDTO {
    private RefundStatus refundStatus;
    private String adminRemarks;
}
