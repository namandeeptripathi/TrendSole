package com.trendsole.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestCreateDTO {
    private Long orderId;
    private Long orderItemId;
    private String reason;
    private String description;
}
