package com.campusgig.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApplicationRequest {

    private String coverMessage;

    @Positive(message = "Proposed price must be positive")
    private Double proposedPrice;

    @Positive(message = "Estimated days must be positive")
    private Integer estimatedDays;
}
