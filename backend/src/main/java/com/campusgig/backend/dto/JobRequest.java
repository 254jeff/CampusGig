package com.campusgig.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private Long categoryId;

    private List<Long> skillIds;

    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be positive")
    private Double budget;

    private String taskType;

    private String location;

    private Boolean remote;

    private LocalDateTime deadline;
}
