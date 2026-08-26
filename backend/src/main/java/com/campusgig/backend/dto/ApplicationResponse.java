package com.campusgig.backend.dto;

import com.campusgig.backend.entity.Application;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String coverMessage;
    private Double proposedPrice;
    private Integer estimatedDays;
    private Application.ApplicationStatus status;
    private LocalDateTime createdAt;
}