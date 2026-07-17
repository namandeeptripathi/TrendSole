package com.trendsole.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestCreateDTO {
    private Long orderId;
    private Long orderItemId;
    private String exchangeReason;
    private String description;
    private String requestedSize;
    private String requestedColor;
}
