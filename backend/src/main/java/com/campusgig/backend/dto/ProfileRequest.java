package com.campusgig.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {

    private String studentId;

    @Size(max = 1000, message = "Bio must be less than 1000 characters")
    private String bio;

    private String university;

    private String course;

    private Integer yearOfStudy;

    private Boolean available;

    private java.util.List<Long> skillIds;
}