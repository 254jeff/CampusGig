package com.campusgig.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String studentId;
    private String bio;
    private String avatarUrl;
    private String university;
    private String course;
    private Integer yearOfStudy;
    private boolean available;
    private Double rating;
    private Integer completedTasks;
    private boolean verified;
    private List<SkillDto> skills;

    @Data
    @Builder
    public static class SkillDto {
        private Long id;
        private String name;
    }
}
