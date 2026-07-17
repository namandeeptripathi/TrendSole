package com.trendsole.dto;

import com.trendsole.model.InspectionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspectionUpdateDTO {
    private InspectionStatus inspectionStatus;
    private String adminRemarks;
}
