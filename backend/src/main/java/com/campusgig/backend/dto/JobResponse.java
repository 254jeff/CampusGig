package com.campusgig.backend.dto;

import com.campusgig.backend.entity.Job;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private Double budget;
    private String taskType;
    private String location;
    private boolean remote;
    private LocalDateTime deadline;
    private Job.JobStatus status;
    private LocalDateTime createdAt;
    private Long postedById;
    private String postedByName;
    private CategoryDto category;
    private List<SkillDto> requiredSkills;
    private int applicationCount;

    @Data
    @Builder
    public static class CategoryDto {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class SkillDto {
        private Long id;
        private String name;
    }
}